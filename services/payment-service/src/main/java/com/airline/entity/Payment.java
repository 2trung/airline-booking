package com.airline.entity;

import com.airline.enums.PaymentGateway;
import com.airline.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
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
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    Long bookingId;

    Double amount;

    @Enumerated(EnumType.STRING)
    PaymentGateway provider;

    String providerPaymentId;
    String transactionId;

    @Enumerated(EnumType.STRING)
    PaymentStatus status;

    String failureReason;
    Instant paidAt;
    String refundId;

    @CreatedDate
    Instant createdAt;
    @LastModifiedDate
    Instant updatedAt;


}
