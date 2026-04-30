package edu.icet.ecom.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 1. Tell Spring this annotation goes on a CLASS (because it compares two fields)
@Target(ElementType.TYPE)
// 2. Tell Spring to keep this rule active while the app is running
@Retention(RetentionPolicy.RUNTIME)
// 3. Tell Spring WHICH class holds the actual validation logic
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRange {

    // The default error message if validation fails
    String message() default "End date must be after the start date";

    // Standard boilerplate required by Spring validation
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}