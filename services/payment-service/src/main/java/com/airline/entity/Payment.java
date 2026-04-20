package com.airline.entity;

import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    Long userId;

    @Column
    Long bookingId;

    Double amount;

    @Enumerated(EnumType.STRING)
    PaymentGateway provider;

    String providerPaymentId;
    String transactionId;
    String method;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    String failureReason;
    Instant paidAt;
    String refundId;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
