package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.FuelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    private String brand;
    private int seatingCapacity;
    private String model;
    private FuelType fuelType;
    private double pricePerDay;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @OneToMany(mappedBy = "car")
    private List<Booking> bookings;

    @Version
    private Long version;
}
