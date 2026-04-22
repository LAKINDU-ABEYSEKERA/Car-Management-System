package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.BookingDTO;
import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.service.BookingService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/createBooking")
    public ResponseEntity<StandardResponse> createBooking(@Valid @RequestBody BookingDTO request) {
        BookingDTO savedBooking = bookingService.createBooking(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new StandardResponse(HttpStatus.CREATED.value(), "Booking successfully created", savedBooking));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PutMapping("/returnCar/{bookingId}")
    public ResponseEntity<StandardResponse> returnCar(
            @PathVariable String bookingId,
            @RequestParam(required = false, defaultValue = "0.0") Double lateFees,
            @RequestParam(required = false, defaultValue = "0.0") Double damageFees
    ) {
        log.info("Return car request received for booking: {}", bookingId);

        BookingDTO completedBooking = bookingService.returnCar(bookingId, lateFees, damageFees);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Car returned successfully. Payment captured.", completedBooking)
        );
    }








    // =========================================================================
    // READ OPERATIONS (STAFF & ADMIN)
    // =========================================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getBooking/{bookingId}")
    public ResponseEntity<StandardResponse> getBooking(@PathVariable String bookingId) {
        BookingDTO booking = bookingService.getBooking(bookingId);
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Booking retrieved successfully", booking)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @GetMapping("/getAllBookings")
    public ResponseEntity<StandardResponse> getAllBookings() {
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "All bookings retrieved", bookingService.getAllBookings())
        );
    }

    // =========================================================================
    // DANGEROUS OPERATIONS (ADMIN ONLY)
    // =========================================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/updateDates/{bookingId}")
    public ResponseEntity<StandardResponse> updateBookingDates(
            @PathVariable String bookingId,
            @RequestParam LocalDate newStartDate,
            @RequestParam LocalDate newEndDate) {

        BookingDTO updatedBooking = bookingService.updateBookingDates(bookingId, newStartDate, newEndDate);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Booking dates updated securely", updatedBooking)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteBooking/{bookingId}")
    public ResponseEntity<StandardResponse> deleteBooking(@PathVariable String bookingId) {
        bookingService.deleteBooking(bookingId);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Booking permanently deleted", null)
        );
    }








    // Helper method (Industry standard: Controllers return DTOs, not Entities)
//    private BookingDTO mapToDto(Booking booking) {
//        BookingDTO dto = new BookingDTO();
//        dto.setCarId(String.valueOf(booking.getCar().getCarId()));
//        dto.setStartDate(booking.getStartDate());
//        dto.setEndDate(booking.getEndDate());
//        // Map other fields as necessary...
//        return dto;
//    }






}