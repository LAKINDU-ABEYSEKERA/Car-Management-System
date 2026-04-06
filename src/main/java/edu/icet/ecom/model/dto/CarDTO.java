package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.FuelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class CarDTO {

    private String carId;
    private String brand;
    private int seatingCapacity;
    private String model;
    private FuelType fuelType;
    private double pricePerDay;
    private CarStatus status;
}
