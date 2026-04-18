package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            SELECT COUNT(b) > 0 
            FROM Booking b
            WHERE b.car.carId = :carId
            AND b.bookingId != :excludeBookingId
            AND b.bookingStatus IN ('PENDING', 'CONFIRMED')
            AND b.startDate < :endDate
            AND b.endDate > :startDate
            """)
    boolean existsOverlappingBooking(
            @Param("carId") Long carId,
            @Param("excludeBookingId") Long excludeBookingId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}