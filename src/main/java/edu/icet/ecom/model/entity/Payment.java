package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private double amount;


}
