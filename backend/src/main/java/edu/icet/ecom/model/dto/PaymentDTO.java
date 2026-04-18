package edu.icet.ecom.model.dto;

import edu.icet.ecom.model.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentDTO {

    // Removed @NotBlank so it can be empty when creating a new payment
    @Pattern(regexp = "^P\\d{3}$", message = "Payment ID must match format P001")
    private String paymentId;

    @NotBlank(message = "Booking ID is required")
    @Pattern(regexp = "^B\\d{3}$", message = "Booking ID must match format B001")
    private String bookingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    // YES! The dates are included here so the frontend can display receipts!
    private LocalDateTime authDate;
    private LocalDateTime captureDate;

    // The enterprise financial fields (calculated by the backend)
    private Double estimatedAmount;
    private Double securityDeposit;
    private Double finalCapturedAmount;

    private String paymentStatus; // e.g., "HOLD_PLACED", "CAPTURED"
}