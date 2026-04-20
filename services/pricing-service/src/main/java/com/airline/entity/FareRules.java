package com.airline.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class FareRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String ruleName;

    @Column()
    Long airlineId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnore
    Fare fare;

    Boolean isRefundable;

    @Column(name = "change_fee")
    Double changeFee;

    @Column(name = "cancellation_fee")
    Double cancellationFee;

    @Column()
    Integer refundDeadlineDays;

    @Column()
    Integer changeDeadlineHours;

    @Builder.Default
    Boolean isChangeable = false;

    @Column(updatable = false)
    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;

}
