package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.DriverStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class DriverDTO {
    private String driverId;
    private String name;
    private String licenceNo;
    private DriverStatus status;
}
