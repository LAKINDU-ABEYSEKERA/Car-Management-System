package edu.icet.ecom.exception;

import edu.icet.ecom.util.StandardResponse;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j // Enables the 'log' object for professional console logging
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Custom Business Logic Errors
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<StandardResponse> handleBusinessException(BusinessException ex){
        log.warn("Business rule violation: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StandardResponse(HttpStatus.CONFLICT.value(), ex.getMessage(), null));
    }

    // 2. Database Concurrency (Two users booking the same car)
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<StandardResponse> handleOptimisticLock(OptimisticLockException ex){
        log.warn("Optimistic lock failure: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new StandardResponse(HttpStatus.CONFLICT.value(), "Car booking conflict, please try again", null));
    }

    // 3. DTO Validation Errors (Empty names, bad emails)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed for incoming request: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error", errors));
    }

    // 4. Bad Formatting (Sending "1" instead of "C001")
    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class})
    public ResponseEntity<StandardResponse> handleBadFormats(Exception ex) {
        log.warn("Invalid data format provided: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new StandardResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Data Format Provided", null));
    }

    // 5. Generic Runtime Errors (e.g., "Customer not found")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardResponse> handleRuntimeExceptions(RuntimeException ex) {
        log.error("Resource error: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new StandardResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null));
    }

    // 6. THE SAFETY NET: Catches anything else so hackers don't see your stack trace
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse> handleAllOtherExceptions(Exception ex) {
        log.error("An unexpected, critical error occurred: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new StandardResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected server error occurred. Please contact support.", null));
    }
}