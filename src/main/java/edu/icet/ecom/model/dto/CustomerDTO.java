package edu.icet.ecom.model.dto;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CustomerDTO {


    private String customerId;
    private String customerName;
    private String address;
    private String email;
}
