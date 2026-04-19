package com.airline.dto;

import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentDTO {
    Long id;

    Long userId;
    String userName;
    String userEmail;

    Long bookingId;
    PaymentStatus status;
    PaymentGateway gateway;
    Double amount;
    String transactionId;
    String gatewayPaymentId;
    String gatewayOrderId;
    String gatewaySignature;

    String description;
    String failureReason;
    Instant initiatedAt;
    Instant completedAt;
    Boolean notificationSent;
    Boolean isActive;
    Instant createdAt;
    Instant updatedAt;

}
