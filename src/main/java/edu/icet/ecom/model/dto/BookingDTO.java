package edu.icet.ecom.model.dto;

import edu.icet.ecom.validation.ValidDateRange;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@ValidDateRange
public class BookingDTO {

    // @Pattern allows null (for creation) but enforces format if provided (for updates)
    @Pattern(regexp = "^B\\d{3}$", message = "Booking ID must match format B001")
    private String bookingId;

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^U\\d{3}$", message = "User ID must match format U001")
    private String userId;

    @NotBlank(message = "Car ID is required")
    private String carId;

    @NotBlank(message = "Customer ID is required")
    @Pattern(regexp = "^C\\d{3}$", message = "Customer ID must match format C001")
    private String customerId;

    // driverId can be null if they drive themselves! No @NotBlank here.
    private String driverId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date cannot be in the past")
    private LocalDate endDate;

    @Positive(message = "Total price must be greater than zero")
    private double totalPrice;

    private boolean withDriver;

    // We don't validate Status here because the Service layer usually sets it to "PENDING" automatically!
    private String bookingStatus;
}