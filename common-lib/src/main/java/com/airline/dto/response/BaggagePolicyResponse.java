package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BaggagePolicyResponse {

    Long id;
    String name;
    String description;

    // Cabin baggage
    Double cabinBaggageMaxWeight;
    Integer cabinBaggagePieces;
    Double cabinBaggageWeightPerPiece;
    Double cabinBaggageMaxDimension;

    // Check-in baggage
    Double checkInBaggageMaxWeight;
    Integer checkInBaggagePieces;
    Double checkInBaggageWeightPerPiece;
    Integer freeCheckedBagsAllowance;

    // Benefits
    Boolean priorityBaggage;
    Boolean extraBaggageAllowance;

    // Relationships
    Long airlineId;
    Long fareId;

    // Audit
    Instant createdAt;
    Instant updatedAt;
}
