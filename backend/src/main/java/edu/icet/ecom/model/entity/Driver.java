package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Driver {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long driverId;
    private String name;
    private String licenceNo;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings;
}
