package com.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class FareRules {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    String ruleName;

    Long airlineId;

    @OneToOne
    Fare fare;

    Boolean isRefundable;

    Boolean isChangeable;

    Double changeFee;

    Double cancellationFee;

    Integer refundableDays;

    Integer changeableHours;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
