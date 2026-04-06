package edu.icet.ecom.model.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class BookingDTO {

    private String bookingId;
    private String userId;
    private String carId;
    private String customerId;
    private String driverId;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private boolean withDriver;
    private String bookingStatus;
}
