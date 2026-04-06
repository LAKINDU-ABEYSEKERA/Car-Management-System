package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PaymentDTO {

    private String paymentId;
    private String bookingId;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private double amount;
}
