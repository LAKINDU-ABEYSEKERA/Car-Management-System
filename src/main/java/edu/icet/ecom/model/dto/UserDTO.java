package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class UserDTO {

    private String userId;
    private String userName;
    private String password;
    private String email;
    private UserStatus role;
}
