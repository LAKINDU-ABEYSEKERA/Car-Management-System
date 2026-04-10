package edu.icet.ecom.validation;

import edu.icet.ecom.model.dto.BookingDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// It implements ConstraintValidator<YourAnnotation, TheClassToValidate>
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, BookingDTO> {

    @Override
    public boolean isValid(BookingDTO dto, ConstraintValidatorContext context) {
        // 1. If either date is missing, let the @NotNull annotations handle the error.
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            return true;
        }

        // 2. The actual business logic!
        // Returns TRUE if the end date is strictly after the start date.
        return dto.getEndDate().isAfter(dto.getStartDate());
    }
}