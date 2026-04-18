package edu.icet.ecom.model.enums;

public enum PaymentStatus {
    HOLD_PLACED, // Money is frozen on the card
    CAPTURED,    // Rental finished, final amount taken, deposit released
    FAILED,      // Card was declined
    REFUNDED     // Booking was cancelled before pickup
}