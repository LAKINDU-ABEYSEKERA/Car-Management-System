package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {

    @Pattern(regexp = "^U\\d{3}$", message = "User ID must match format U001")
    private String userId;

    @NotBlank(message = "Username cannot be empty")
    private String userName;

    // Ensures password is at least 8 chars, has 1 uppercase, 1 lowercase, and 1 number
   // @NotBlank(message = "Password cannot be empty")
   // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
    //        message = "Password must be at least 8 characters with 1 uppercase, 1 lowercase, and 1 number")
    //private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "User role/status is required")
    private UserStatus role;
}