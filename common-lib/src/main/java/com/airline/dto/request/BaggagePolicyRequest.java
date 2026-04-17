package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BaggagePolicyRequest {

    @NotBlank(message = "Baggage policy name is required")
    String name;

    @NotNull(message = "Fare ID is required")
    Long fareId;

    Long airlineId;

    String description;

    @PositiveOrZero(message = "Baggage weight per piece must be a positive or zero value")
    Double cabinBaggageMaxWeight;

    @PositiveOrZero(message = "Cabin baggage dimension must be a positive or zero value")
    Double cabinBaggageMaxDimension;

    @PositiveOrZero(message = "Cabin baggage pieces must be a positive or zero value")
    Integer cabinBaggagePieces;

    @PositiveOrZero(message = "Check-in baggage weight per piece must be a positive or zero value")
    Double checkInBaggageMaxWeight;

    @PositiveOrZero(message = "Check-in baggage pieces must be a positive or zero value")
    Integer checkInBaggagePieces;

    @PositiveOrZero(message = "Check-in baggage weight per piece must be a positive or zero value")
    Double checkInBaggageWeightPerPiece;

    @PositiveOrZero(message = "Free checked bags allowed must be a positive or zero value")
    Integer freeCheckedBagsAllowed;

    Boolean priorityBaggageAllowed;

    Boolean extraBaggageAllowed;
}
