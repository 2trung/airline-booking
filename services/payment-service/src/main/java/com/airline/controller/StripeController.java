package com.airline.controller;

import com.airline.dto.request.PaymentVerifyRequest;
import com.airline.enums.PaymentStatus;
import com.airline.service.gateway.StripeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stripe")
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/verify")
    public ResponseEntity<PaymentStatus> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        return ResponseEntity.ok(stripeService.verifyPayment(request.getStripePaymentIntentId()));
    }
}

