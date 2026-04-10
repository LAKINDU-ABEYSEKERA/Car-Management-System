package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.BookingDTO;
import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody BookingDTO bookingDTO) {
        log.info("Received request to create booking for car ID: {}", bookingDTO.getCarId());

        Booking savedBooking = bookingService.createBooking(bookingDTO);

        // Convert the returned Entity back to a DTO before sending to the client
        BookingDTO responseDTO = mapToDto(savedBooking);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // Helper method (Industry standard: Controllers return DTOs, not Entities)
    private BookingDTO mapToDto(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setCarId(String.valueOf(booking.getCar().getCarId()));
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        // Map other fields as necessary...
        return dto;
    }
}