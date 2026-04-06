package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
public class User {

    @Id
    private String userId;
    private String userName;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus role;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}
