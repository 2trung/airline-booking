package com.airline.client;

import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.response.PaymentInitiateResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentInitiateResponse initiatePayment(@Valid @RequestBody PaymentInitiateRequest request);
}
