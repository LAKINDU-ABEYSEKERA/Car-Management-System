package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("""
            SELECT COUNT(b) > 0 
            FROM Booking b
            WHERE b.car.id = :carId
            AND b.bookingStatus IN ('PENDING','PAID','COMPLETED')
            AND b.startDate < :endDate
            AND b.endDate > :startDate
            """)

            boolean existsOverlappingBooking(
                Long carId,
                LocalDate startDate,
                LocalDate endDate
            );

}
