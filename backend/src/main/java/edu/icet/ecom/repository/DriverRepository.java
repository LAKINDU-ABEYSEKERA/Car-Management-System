package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Driver;
import edu.icet.ecom.model.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional; // <-- Make sure this is imported!

public interface DriverRepository extends JpaRepository<Driver, Long> {

    // Spring Boot automatically translates this method name into a SQL query!
    Optional<Driver> findFirstByStatus(DriverStatus status);
}