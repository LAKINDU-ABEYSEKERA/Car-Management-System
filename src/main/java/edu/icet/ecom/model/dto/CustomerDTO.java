package edu.icet.ecom.model.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class CustomerDTO {

    @Pattern(regexp = "^C\\d{3}$", message = "Customer ID must match format C001")
    private String customerId;

    @NotBlank(message = "Customer name can not be empty")
    private String customerName;

    @NotBlank(message = "Address can not be empty")
    private String address;

    @NotBlank(message = "Email can not be empty")
    @Email(message = "Invalid email format")
    private String email;
}
