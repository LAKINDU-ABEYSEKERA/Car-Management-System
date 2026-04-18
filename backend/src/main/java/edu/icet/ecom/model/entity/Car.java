package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.FuelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.processing.SQL;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@EntityListeners(AuditingEntityListener.class) // 1. Tells Spring to watch this class for Auditing
@SQLDelete(sql = "UPDATE car SET is_active = false WHERE car_id = ? AND version = ?") // 2. Overrides the hard DELETE command
@SQLRestriction("is_active = true") // 3. Hides deleted cars from all SELECT queries
public class Car extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    private String brand;
    private int seatingCapacity;
    private String model;

    @Enumerated(EnumType.STRING)
    private FuelType fuelType;
    private double pricePerDay;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @OneToMany(mappedBy = "car")
    private List<Booking> bookings;

    @Version
    private Long version;

    // ==========================================
    // ENTERPRISE UPGRADE: SOFT DELETES
    // ==========================================
    @Column(nullable = false)
    private boolean isActive = true; // Defaults to true when a car is created

    // ==========================================
    // ENTERPRISE UPGRADE: JPA AUDITING
    // ==========================================
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // Automatically stamps when the row is created

    @LastModifiedDate
    private LocalDateTime updatedAt; // Automatically updates whenever the row changes
}
