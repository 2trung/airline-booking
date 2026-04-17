package com.airline.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
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
@EntityListeners(AuditingEntityListener.class)
public class BaggagePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @OneToOne
    @JsonIgnore
    Fare fare;

    String name;

    String description;

    Double cabinBaggageMaxWeight;

    Integer cabinBaggageMaxDimension;

    Integer cabinBaggagePieces = 1;

    Double checkInBaggageMaxWeight;

    Integer checkInBaggagePieces = 1;

    Double checkInBaggageWeightPerPiece;

    Integer freeCheckedBagsAllowed = 0;

    Boolean priorityBaggageAllowed = false;

    Boolean extraBaggageAllowed = false;

    Long flightId;
    Long airlineId;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
