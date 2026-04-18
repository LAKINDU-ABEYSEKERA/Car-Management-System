package edu.icet.ecom.model.entity;

import edu.icet.ecom.model.enums.PaymentMethod;
import edu.icet.ecom.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Payment extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // Uses the Enum safely!

    private LocalDateTime authDate; // Exactly when the hold was placed
    private LocalDateTime captureDate; // Exactly when the return was processed

    private Double estimatedAmount;
    private Double securityDeposit;
    private Double finalCapturedAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}