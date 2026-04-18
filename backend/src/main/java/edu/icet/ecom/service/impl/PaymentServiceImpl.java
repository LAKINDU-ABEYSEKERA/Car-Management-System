package edu.icet.ecom.service.impl;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.PaymentDTO;
import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.model.entity.Car;
import edu.icet.ecom.model.entity.Payment;
import edu.icet.ecom.model.enums.BookingStatus;
import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.PaymentStatus;
import edu.icet.ecom.repository.BookingRepository;
import edu.icet.ecom.repository.CarRepository;
import edu.icet.ecom.repository.PaymentRepository;
import edu.icet.ecom.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;

    // The standard security deposit applied to all rentals
    private static final Double STANDARD_DEPOSIT = 500.00;

    @Override
    public PaymentDTO authorizePayment(PaymentDTO request) {
        log.info("Initiating Pre-Authorization Hold for Booking: {}", request.getBookingId());

        // 1. Extract the actual Booking ID
        Long actualBookingId = extractId(request.getBookingId(), "B");

        // 2. Fetch the Booking (Fail Fast if missing)
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + request.getBookingId()));

        // 3. Prevent double payments / wrong state payments
        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Cannot authorize payment. Booking is currently " + booking.getBookingStatus());
        }

        // 4. Create and Save the Payment Hold (Auth)
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAuthDate(LocalDateTime.now());

        // Set up the Enterprise Financials
        payment.setEstimatedAmount(booking.getTotalPrice());
        payment.setSecurityDeposit(STANDARD_DEPOSIT);
        payment.setFinalCapturedAmount(0.0); // Zero because it is just a hold!
        payment.setPaymentStatus(PaymentStatus.HOLD_PLACED);

        Payment savedPayment = paymentRepository.save(payment);

        // 5. UPDATE THE STATE MACHINE
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        Car car = booking.getCar();
        car.setStatus(CarStatus.BOOKED);

        bookingRepository.save(booking);
        carRepository.save(car);

        log.info("HOLD PLACED: Frozen ${} for Booking {}. Car {} is now BOOKED.",
                (booking.getTotalPrice() + STANDARD_DEPOSIT), booking.getBookingId(), car.getCarId());

        return mapToDTO(savedPayment);
    }










    @Override
    public PaymentDTO refundPayment(String bookingId) {
        log.warn("Initiating refund process for Booking ID: {}", bookingId);

        Long actualBookingId = extractId(bookingId, "B");
        Booking booking = bookingRepository.findById(actualBookingId)
                .orElseThrow(() -> new BusinessException("Booking not found with ID: " + bookingId));

        Payment payment = paymentRepository.findByBooking(booking)
                .orElseThrow(() -> new BusinessException("No payment record found for this booking."));

        // 1. FINANCIAL VALIDATION
        if (payment.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("This payment has already been refunded!");
        }

        // 2. EXECUTE THE REFUND (We don't delete the row, we update its state securely)
        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setFinalCapturedAmount(0.0); // We took no money
        paymentRepository.save(payment);

        // 3. CANCEL BOOKING & FREE THE CAR
        booking.setBookingStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        Car car = booking.getCar();
        car.setStatus(CarStatus.AVAILABLE);
        carRepository.save(car);

        log.info("Refund successful. Booking {} cancelled and Car {} is AVAILABLE.", bookingId, car.getCarId());

        return mapToDTO(payment);
    }






    // =========================================================================
    // PRIVATE HELPER METHODS
    // =========================================================================

    private PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(String.format("P%03d", payment.getPaymentId()));
        dto.setBookingId(String.format("B%03d", payment.getBooking().getBookingId()));
        dto.setPaymentMethod(payment.getPaymentMethod());

        dto.setAuthDate(payment.getAuthDate());
        dto.setCaptureDate(payment.getCaptureDate());

        dto.setEstimatedAmount(payment.getEstimatedAmount());
        dto.setSecurityDeposit(payment.getSecurityDeposit());
        dto.setFinalCapturedAmount(payment.getFinalCapturedAmount());

        dto.setPaymentStatus(payment.getPaymentStatus().name());
        return dto;
    }

    private Long extractId(String formattedId, String prefix) {
        if (formattedId == null || !formattedId.startsWith(prefix)) {
            throw new BusinessException("Invalid ID format. Must start with '" + prefix + "'");
        }
        return Long.parseLong(formattedId.substring(prefix.length()));
    }
}