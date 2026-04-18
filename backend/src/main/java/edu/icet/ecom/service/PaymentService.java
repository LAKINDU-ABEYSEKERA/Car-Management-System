package edu.icet.ecom.service;

import edu.icet.ecom.model.dto.PaymentDTO;

public interface PaymentService {

    // The Enterprise Auth & Capture start point!
    PaymentDTO authorizePayment(PaymentDTO request);
    PaymentDTO refundPayment(String bookingId);
}