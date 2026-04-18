package edu.icet.ecom.repository;

import edu.icet.ecom.model.entity.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends JpaRepository<Car,Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT c FROM Car c WHERE c.id = :carId
            """)
    Car lockCar(Long carId);
}
