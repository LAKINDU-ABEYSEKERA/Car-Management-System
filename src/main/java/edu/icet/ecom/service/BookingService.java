package edu.icet.ecom.service;

import edu.icet.ecom.exception.BusinessException;
import edu.icet.ecom.model.dto.BookingDTO;
import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.model.enums.BookingStatus;
import edu.icet.ecom.repository.BookingRepository;
import edu.icet.ecom.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Booking createBooking(BookingDTO dto){
        carRepository.lockCar(Long.valueOf(dto.getCarId()));

        boolean booked = bookingRepository.existsOverlappingBooking(
                Long.valueOf(dto.getCarId()),
                dto.getStartDate(),
                dto.getEndDate()
        );

        if (booked) {
            throw new BusinessException("Car already booked");
        }

        Booking booking =  new Booking();
        booking.setCar(carRepository.getReferenceById(Long.valueOf(dto.getCarId())));
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setBookingStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }


}
