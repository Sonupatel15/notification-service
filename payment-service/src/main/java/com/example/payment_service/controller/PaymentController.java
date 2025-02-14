package com.example.payment_service.controller;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // It's better to use a dedicated endpoint for updating payment status
    // instead of a generic PUT request.
    @PutMapping("/{id}/status")
    public ResponseEntity<Payment> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, PaymentStatus.valueOf(status)));
    }


    @PostMapping("/process") // New endpoint to process payment
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequest paymentRequest) {
        Payment payment = paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok(payment);
    }

}
