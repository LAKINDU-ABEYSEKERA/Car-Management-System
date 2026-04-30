package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.DriverStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DriverDTO {

    @Pattern(regexp = "^D\\d{3}$", message = "Driver ID must match format D001")
    private String driverId;

    @NotBlank(message = "Driver name cannot be empty")
    private String name;

    @NotBlank(message = "License number cannot be empty")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "License number contains invalid characters")
    private String licenceNo;

    @NotNull(message = "Driver status is required")
    private DriverStatus status;
}