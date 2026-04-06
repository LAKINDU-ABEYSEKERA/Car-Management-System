package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class Payment {

    @Id
    private String paymentId;

    @OneToOne
    @JoinColumn(name = "bookingId")
    private Booking booking;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private double amount;


}
