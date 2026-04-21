package com.airline.client;

import com.airline.dto.PaymentDTO;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.response.PaymentInitiateResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "payment-service", fallback = PaymentClientFallback.class)
public interface PaymentClient {

    @PostMapping("/api/payments/initiate")
    PaymentInitiateResponse initiatePayment(
            @Valid @RequestBody PaymentInitiateRequest request,
            @RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/payments/booking/{bookingId}")
    PaymentDTO getPaymentByBookingId(@PathVariable Long bookingId);

    @PostMapping("/api/payments/batch/bookings")
    Map<Long, PaymentDTO> getPaymentsByBookingIds(@RequestBody List<Long> bookingIds);
}
