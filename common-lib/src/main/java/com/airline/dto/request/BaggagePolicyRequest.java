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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BaggagePolicyRequest {

    @NotBlank(message = "Policy name is required")
    String name;

    @NotNull(message = "Fare ID is required")
    Long fareId;

    String description;

    // Cabin baggage
    @PositiveOrZero(message = "Cabin baggage max weight must be positive or zero")
    Double cabinBaggageMaxWeight;

    @PositiveOrZero(message = "Cabin baggage pieces must be positive or zero")
    Integer cabinBaggagePieces;

    @PositiveOrZero(message = "Cabin baggage weight per piece must be positive or zero")
    Double cabinBaggageWeightPerPiece;

    @PositiveOrZero(message = "Cabin baggage max dimension must be positive or zero")
    Double cabinBaggageMaxDimension;

    // Check-in baggage
    @PositiveOrZero(message = "Check-in baggage max weight must be positive or zero")
    Double checkInBaggageMaxWeight;

    @PositiveOrZero(message = "Check-in baggage max dimension must be positive or zero")
    Integer checkInBaggagePieces;

    @PositiveOrZero(message = "Check-in baggage weight per piece must be positive or zero")
    Double checkInBaggageWeightPerPiece;

    @PositiveOrZero(message = "Check-in baggage max dimension must be positive or zero")
    Integer freeCheckedBagsAllowance;

    // Benefits
    Boolean priorityBaggage;
    Boolean extraBaggageAllowance;

}
