package edu.icet.ecom.event;

// A Record is just a class where all fields are final and getters are automatically generated!
public record BookingCreatedEvent(
        String bookingId,
        String customerName,
        String customerEmail
) {}