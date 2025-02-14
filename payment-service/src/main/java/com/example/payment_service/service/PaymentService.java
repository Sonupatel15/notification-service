package com.example.payment_service.service;

import com.example.payment_service.dto.PaymentRequest;
import com.example.payment_service.exception.PaymentFailedException;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.model.Payment;
import com.example.payment_service.model.PaymentStatus;
import com.example.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate; // Import RestTemplate

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate; // Use Object for value
    private final RestTemplate restTemplate; // Inject RestTemplate

    @Transactional
    public Payment createPayment(Payment payment) {
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment); // Save first to get the ID
        log.info("Payment created with ID: {}", payment.getId()); // Log the ID

        return payment;
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentStatus(status);
        payment = paymentRepository.save(payment);
        log.info("Payment status updated to {} for ID: {}", status, paymentId);
        return payment;
    }


    @Transactional
    public Payment processPayment(PaymentRequest paymentRequest) {
        // 1. Create a pending payment record
        Payment payment = Payment.builder()
                .userId(paymentRequest.getUserId())
                .amount(paymentRequest.getAmount())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        payment = createPayment(payment); // Save the pending payment

        try {
            // 2. Call external payment gateway (mocked here)
            String paymentGatewayResponse = callPaymentGateway(paymentRequest);

            if ("SUCCESS".equals(paymentGatewayResponse)) {
                // 3. Update payment status to SUCCESS
                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                updatePaymentStatus(payment.getId(), PaymentStatus.SUCCESS); // Update status

                // 4. Publish Kafka event for successful payment
                kafkaTemplate.send("payment_events", payment); // Send the payment object

                log.info("Payment processed successfully for user: {}", paymentRequest.getUserId());
                return payment;

            } else {
                // 5. Update payment status to FAILED
                payment.setPaymentStatus(PaymentStatus.FAILED);
                updatePaymentStatus(payment.getId(), PaymentStatus.FAILED);

                // 6. Publish Kafka event for failed payment (optional)
                // kafkaTemplate.send("payment_failed_events", payment);

                throw new PaymentFailedException("Payment failed: " + paymentGatewayResponse);
            }

        } catch (Exception e) {
            // 7. Handle exceptions (e.g., retry, notify admin)
            payment.setPaymentStatus(PaymentStatus.FAILED);
            updatePaymentStatus(payment.getId(), PaymentStatus.FAILED);
            log.error("Payment processing failed: {}", e.getMessage());
            throw new PaymentFailedException("Payment processing failed: " + e.getMessage());
        }
    }

    private String callPaymentGateway(PaymentRequest paymentRequest) {
        // Mock payment gateway call.  Replace with actual integration.
        // Simulate success or failure based on some logic (e.g., amount).
        if (paymentRequest.getAmount().compareTo(BigDecimal.valueOf(100)) < 0) { // Example: Fail if amount < 100
            return "FAILED"; // Or a more specific error code
        } else {
            return "SUCCESS";
        }
    }

    // ... other methods (deductFare, calculateFinalFare, etc.) if needed ...

    public Payment processMetroPayment(Long userId, double amount) {
        // Check metro card balance via User Service
        try {
            MetroCard card = restTemplate.getForObject(
                    "http://user-service/cards/user/" + userId, // Correct URL
                    MetroCard.class
            );

            if (card == null || !card.isActive() || card.getBalance() < amount) { // Check card status
                throw new PaymentFailedException("Insufficient metro card balance or invalid card");
            }

            // Deduct balance (update via PUT request)
            card.setBalance(card.getBalance() - amount);
            restTemplate.put("http://user-service/cards", card); // Correct URL

            // Create payment record
            Payment payment = Payment.builder()
                    .userId(userId)
                    .amount(BigDecimal.valueOf(amount))
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();

            paymentRepository.save(payment);

            // Publish Kafka event (send the Payment object)
            kafkaTemplate.send("payment_events", payment);

            return payment;

        } catch (Exception e) {
            log.error("Metro payment failed: {}", e.getMessage());
            throw new PaymentFailedException("Metro payment failed: " + e.getMessage());
        }
    }
}
