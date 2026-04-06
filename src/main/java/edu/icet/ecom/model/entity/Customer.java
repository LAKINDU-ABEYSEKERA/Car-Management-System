package edu.icet.ecom.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;
    private String customerName;
    private String address;
    private String email;

    @OneToMany(mappedBy = "customer" ,cascade = {CascadeType.PERSIST , CascadeType.MERGE} )
    private List<Booking> bookings;
}
