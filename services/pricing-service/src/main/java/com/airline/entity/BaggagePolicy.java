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
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaggagePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @JsonIgnore
    Fare fare;

    @Column(nullable = false)
    String name;

    String description;

    // Cabin baggage
    Double cabinBaggageMaxWeight;

    @Builder.Default
    Integer cabinBaggagePieces = 1;

    Double cabinBaggageWeightPerPiece;
    Double cabinBaggageMaxDimension;

    // Check-in baggage
    Double checkInBaggageMaxWeight;

    @Builder.Default
    Integer checkInBaggagePieces = 1;

    Double checkInBaggageWeightPerPiece;

    @Builder.Default
    Integer freeCheckedBagsAllowance = 0;

    @Builder.Default
    @Column(nullable = false)
    Boolean priorityBaggage = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean extraBaggageAllowance = false;

    @Column()
    Long airlineId;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    Instant updatedAt;
}
