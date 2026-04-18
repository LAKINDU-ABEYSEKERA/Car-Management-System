package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.CarStatus;
import edu.icet.ecom.model.enums.FuelType;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CarDTO {

    @Pattern(regexp = "^CAR\\d{3}$", message = "Car ID must match format CAR001")
    private String carId;

    @NotBlank(message = "Car brand cannot be empty")
    private String brand;

    @Min(value = 2, message = "Car must have at least 2 seats")
    @Max(value = 15, message = "Car cannot have more than 15 seats")
    private int seatingCapacity;

    @NotBlank(message = "Car model cannot be empty")
    private String model;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @Positive(message = "Price per day must be greater than zero")
    private double pricePerDay;

    @NotNull(message = "Car status is required")
    private CarStatus status;
}