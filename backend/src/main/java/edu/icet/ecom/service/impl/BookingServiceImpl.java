package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.BookingDTO;
import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.model.entity.Car;
import edu.icet.ecom.model.entity.Customer;
import edu.icet.ecom.model.entity.Driver;
import edu.icet.ecom.model.entity.Payment;
import edu.icet.ecom.model.entity.User;
import edu.icet.ecom.model.enums.BookingStatus;
import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.DriverStatus;
import edu.icet.ecom.model.enums.PaymentStatus;
import edu.icet.ecom.repository.BookingRepository;
import edu.icet.ecom.repository.CarRepository;
import edu.icet.ecom.repository.CustomerRepository;
import edu.icet.ecom.repository.DriverRepository; // <-- 1. REQUIRED TO FIND DRIVERS
import edu.icet.ecom.repository.PaymentRepository;
import edu.icet.ecom.repository.UserRepository;
import edu.icet.ecom.service.BookingService;
import edu.icet.ecom.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final DriverRepository driverRepository; // <-- INJECTED HERE
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public BookingDTO createBooking(BookingDTO request) {
        log.info("Processing new booking request for Car: {}", request.getCarId());

        String staffEmail = SecurityUtil.getCurrentUserEmail();
        User loggedInStaff = userRepository.findByEmail(staffEmail)
                .orElseThrow(() -> new BusinessException("CRITICAL: Authenticated staff member not found in DB!"));

        Long actualCarId = extractId(request.getCarId(), "CAR");
        Long actualCustomerId = extractId(request.getCustomerId(), "C");

        Car car = carRepository.findById(actualCarId)
                .orElseThrow(() -> new BusinessException("Car not found with ID: " + request.getCarId()));

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new BusinessException("Car is currently " + car.getStatus() + " and cannot be booked.");
        }

        Customer customer = customerRepository.findById(actualCustomerId)
                .orElseThrow(() -> new BusinessException("Customer not found with ID: " + request.getCustomerId()));

        Booking booking = new Booking();
        booking.setCar(car);
        booking.setCustomer(customer);
        booking.setUser(loggedInStaff);

        booking.setStartDate(request.getStartDate());
        booking.setEndDate(request.getEndDate());
        booking.setTotalPrice(request.getTotalPrice());
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setWithDriver(request.isWithDriver());

        // ====================================================================
        // 2. THE AUTO-DISPATCH ALGORITHM
        // ====================================================================
        if (request.isWithDriver()) {
            log.info("Chauffeur requested. Searching for available driver...");

            // Finds the first driver who isn't already driving someone else
            Driver availableDriver = driverRepository.findFirstByStatus(DriverStatus.AVAILABLE)
                    .orElseThrow(() -> new BusinessException("Booking Failed: No drivers are currently available for dispatch."));

            // Sets the Driver ID in the Database!
            booking.setDriver(availableDriver);

            // Lock the driver so they don't get double-booked
            availableDriver.setStatus(DriverStatus.ASSIGNED);
            driverRepository.save(availableDriver);

            log.info("Driver {} automatically dispatched for this booking.", availableDriver.getDriverId());
        }

        Booking savedBooking = bookingRepository.save(booking);

        eventPublisher.publishEvent(new edu.icet.ecom.event.BookingCreatedEvent(
                String.format("B%03d", savedBooking.getBookingId()),
                customer.getCustomerName(),
                customer.getEmail()
        ));

        return mapToDTO(savedBooking);
    }

    @Override
    public BookingDTO returnCar(String bookingId, Double lateFees, Double damageFees) {
        log.info("Processing car return and Payment Capture for Booking ID: {}", bookingId);

        Long actualBookingId = extractId(bookingId, "B");
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() != BookingStatus.CONFIRMED) {
            throw new BusinessException("Cannot return car. Booking is not in CONFIRMED state.");
        }

        Payment payment = paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new BusinessException("CRITICAL: No payment hold found for this booking!"));

        if (payment.getPaymentStatus() != PaymentStatus.HOLD_PLACED) {
            throw new BusinessException("Payment has already been processed or refunded.");
        }

        Double finalCost = payment.getEstimatedAmount() + (lateFees != null ? lateFees : 0.0) + (damageFees != null ? damageFees : 0.0);
        Double maximumAuthorized = payment.getEstimatedAmount() + payment.getSecurityDeposit();

        if (finalCost > maximumAuthorized) {
            log.warn("Damages exceed security deposit! Charging maximum authorized amount.");
            finalCost = maximumAuthorized;
        }

        payment.setFinalCapturedAmount(finalCost);
        payment.setCaptureDate(LocalDateTime.now());
        payment.setPaymentStatus(PaymentStatus.CAPTURED);
        paymentRepository.save(payment);

        booking.setBookingStatus(BookingStatus.COMPLETED);

        Car car = booking.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);

        // ====================================================================
        // 3. RELEASE THE DRIVER
        // ====================================================================
        if (booking.isWithDriver() && booking.getDriver() != null) {
            Driver driver = booking.getDriver();
            driver.setStatus(DriverStatus.AVAILABLE);
            driverRepository.save(driver);
            log.info("Driver {} released back to the available pool.", driver.getDriverId());
        }

        bookingRepository.save(booking);

        log.info("Car return successful. Booking {} is COMPLETED and Car {} is AVAILABLE.", bookingId, car.getCarId());

        return mapToDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBooking(String bookingId) {
        Long actualBookingId = extractId(bookingId, "B");
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + bookingId));
        return mapToDTO(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public BookingDTO updateBookingDates(String bookingId, LocalDate newStartDate, LocalDate newEndDate) {
        Long actualBookingId = extractId(bookingId, "B");
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() == BookingStatus.COMPLETED) {
            throw new BusinessException("Cannot update a booking that is already COMPLETED.");
        }

        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                booking.getCar().getCarId(),
                actualBookingId,
                newStartDate,
                newEndDate
        );

        if (isOverlapping) {
            throw new BusinessException("Cannot update: The car is already booked during these new dates.");
        }

        booking.setStartDate(newStartDate);
        booking.setEndDate(newEndDate);

        Booking updatedBooking = bookingRepository.save(booking);
        return mapToDTO(updatedBooking);
    }

    @Override
    public void deleteBooking(String bookingId) {
        Long actualBookingId = extractId(bookingId, "B");
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + bookingId));

        if (booking.getBookingStatus() != BookingStatus.COMPLETED) {
            Car car = booking.getCar();
            car.setStatus(CarStatus.AVAILABLE);
            carRepository.save(car);

            // Release the driver if booking is deleted early
            if (booking.isWithDriver() && booking.getDriver() != null) {
                Driver driver = booking.getDriver();
                driver.setStatus(DriverStatus.AVAILABLE);
                driverRepository.save(driver);
            }
        }

        bookingRepository.delete(booking);
    }

    private BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(String.format("B%03d", booking.getBookingId()));
        dto.setCarId(String.format("CAR%03d", booking.getCar().getCarId()));
        dto.setCustomerId(String.format("C%03d", booking.getCustomer().getCustomerId()));

        // Pass the actual Driver ID back to the Angular frontend
        if (booking.getDriver() != null) {
            dto.setDriverId(String.format("D%03d", booking.getDriver().getDriverId()));
        }

        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setWithDriver(booking.isWithDriver());
        dto.setBookingStatus(booking.getBookingStatus().name());
        return dto;
    }

    private Long extractId(String formattedId, String prefix) {
        if (formattedId == null || !formattedId.startsWith(prefix)) {
            throw new BusinessException("Invalid ID format. Must start with '" + prefix + "'");
        }
        return Long.parseLong(formattedId.substring(prefix.length()));
    }
}