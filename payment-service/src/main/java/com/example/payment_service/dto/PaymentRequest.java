package com.example.payment_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod; // e.g., "CARD", "QR"
    // Add other fields as needed for different payment methods
}
