package com.airline.event;

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
public class PaymentFailedEvent {
    Long paymentId;
    Long bookingId;
    Long userId;
    Double amount;
    String transactionId;
    String failureReason;
    Instant failedAt;
}
