package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Booking;
import edu.icet.ecom.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment , Long> {

    // Because we return an Optional, our Service can safely use .orElseThrow()
    Optional<Payment> findByBooking(Booking booking);
}
