package com.example.user_service.client;

import com.example.user_service.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "http://localhost:8083/payments")
public interface PaymentClient {

    @PostMapping
    String processPayment(@RequestBody PaymentRequest request);
}
