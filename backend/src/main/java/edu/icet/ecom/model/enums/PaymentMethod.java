package edu.icet.ecom.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import edu.icet.ecom.exception.BusinessException;

public enum PaymentMethod {
    CREDIT_CARD,
    CASH,
    BANK_TRANSFER;

    @JsonCreator // Tells Jackson to route all incoming JSON strings through this method
    public static PaymentMethod fromString(String value) {
        if (value == null) {
            return null;
        }
        for (PaymentMethod method : PaymentMethod.values()) {
            // Check if it matches, completely ignoring case!
            if (method.name().equalsIgnoreCase(value.trim())) {
                return method;
            }
        }
        // If they send garbage data, we throw OUR clean exception, not Jackson's ugly one!
        throw new BusinessException("Invalid Payment Method: '" + value + "'. Accepted values are CREDIT_CARD, CASH, or BANK_TRANSFER.");
    }
}