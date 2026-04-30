package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDTO {

    @Pattern(regexp = "^P\\d{3}$", message = "Payment ID must match format P001")
    private String paymentId;

    @NotBlank(message = "Booking ID is required")
    @Pattern(regexp = "^B\\d{3}$", message = "Booking ID must match format B001")
    private String bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @Positive(message = "Payment amount must be greater than zero")
    private double amount;
}