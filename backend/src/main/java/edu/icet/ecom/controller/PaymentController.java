package edu.icet.ecom.controller;

import edu.icet.ecom.model.dto.PaymentDTO;
import edu.icet.ecom.service.PaymentService;
import edu.icet.ecom.util.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // =========================================================================
    // MAIN PAYMENT OPERATION (STAFF & ADMIN)
    // =========================================================================

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @PostMapping("/authorize")
    public ResponseEntity<StandardResponse> authorizePayment(@Valid @RequestBody PaymentDTO request) {
        log.info("Processing payment hold for Booking ID: {}", request.getBookingId());

        PaymentDTO authorizedPayment = paymentService.authorizePayment(request);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Payment hold placed successfully", authorizedPayment)
        );
    }

    // =========================================================================
    // IMMUTABLE LEDGER OPERATION (ADMIN ONLY)
    // =========================================================================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/refund/{bookingId}")
    public ResponseEntity<StandardResponse> refundPayment(@PathVariable String bookingId) {
        log.warn("Admin initiating secure refund for Booking ID: {}", bookingId);

        PaymentDTO refundedPayment = paymentService.refundPayment(bookingId);

        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Payment refunded and booking cancelled securely", refundedPayment)
        );
    }
}