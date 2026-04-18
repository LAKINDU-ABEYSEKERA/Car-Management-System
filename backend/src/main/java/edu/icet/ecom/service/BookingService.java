package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.BookingDTO;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDTO createBooking(BookingDTO request);
    BookingDTO returnCar(String bookingId, Double lateFees, Double damageFees);

    // NEW CRUD METHODS
    BookingDTO getBooking(String bookingId);
    List<BookingDTO> getAllBookings();
    BookingDTO updateBookingDates(String bookingId, LocalDate newStartDate, LocalDate newEndDate);
    void deleteBooking(String bookingId);
}