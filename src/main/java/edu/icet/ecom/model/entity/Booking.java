package edu.icet.ecom.model.entity;


import edu.icet.ecom.model.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String bookingId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "carId")
    private Car car;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driverId")
    private Driver driver;

    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private boolean withDriver;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

}
