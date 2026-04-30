package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver,Long> {
}
