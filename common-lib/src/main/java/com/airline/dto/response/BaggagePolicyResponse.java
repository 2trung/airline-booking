package com.airline.dto.response;

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
public class BaggagePolicyResponse {
    Long id;

    String name;
    String description;

    Double cabinBaggageMaxWeight;
    Integer cabinBaggagePieces;
    Double checkInBaggageMaxWeight;
    Integer cabinBaggageMaxDimension;

    Integer checkInBaggagePieces;
    Double checkInBaggageWeightPerPiece;
    Integer freeCheckedBagsAllowed;

    Boolean priorityBaggageAllowed;
    Boolean extraBaggageAllowed;

    Long flightId;
    Long airlineId;

    Instant createdAt;
    Instant updatedAt;
}
