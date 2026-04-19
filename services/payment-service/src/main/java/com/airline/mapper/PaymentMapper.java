package com.airline.mapper;

import com.airline.dto.PaymentDTO;
import com.airline.dto.response.PaymentInitiateResponse;
import com.airline.entity.Payment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentDTO toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDTO.builder()
                        .id(payment.getId())
                .userId(payment.getUserId())
                .bookingId(payment.getBookingId())
                .status(payment.getStatus())
                .gateway(payment.getProvider())
                .amount(payment.getAmount() == null ? null : payment.getAmount())
                .transactionId(payment.getTransactionId())
                .gatewayPaymentId(payment.getProviderPaymentId())
                .failureReason(payment.getFailureReason())
                .initiatedAt(payment.getCreatedAt())
                .completedAt(payment.getPaidAt())
                .isActive(Boolean.TRUE)
                .completedAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public static PaymentInitiateResponse toInitiateResponse(Payment payment, String description) {
        if (payment == null) {
            return null;
        }

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .gateway(payment.getProvider())
                .amount(payment.getAmount())
                .description(description)
                .checkOutUrl("/api/payments/checkout/" + payment.getProviderPaymentId())
                .message("Payment initiated successfully")
                .success(Boolean.TRUE)
                .build();
    }

    private static LocalDateTime toUtcLocalDateTime(Instant instant) {
        if (instant == null) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}
