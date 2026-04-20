package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class FareRequest {

    @NotBlank(message = "Fare name is required")
    String name;

    @NotNull(message = "RBD code is required")
    Character rbdCode;

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Cabin class ID is required")
    Long cabinClassId;

    // Pricing
    @NotNull(message = "Base fare is required")
    @Positive
    Double baseFare;

    Double taxesAndFees;
    Double airlineFees;
    Double currentPrice;

    @Size(max = 100)
    String fareLabel;

    // Seat benefits
    Boolean extraSeatSpace;
    Boolean preferredSeatChoice;
    Boolean advanceSeatSelection;
    Boolean guaranteedSeatTogether;

    // Boarding benefits
    Boolean priorityBoarding;
    Boolean priorityCheckin;
    Boolean fastTrackSecurity;

    // In-flight benefits
    Boolean complimentaryMeals;
    Boolean premiumMealChoice;
    Boolean inFlightInternet;
    Boolean inFlightEntertainment;
    Boolean complimentaryBeverages;

    // Flexibility benefits
    Boolean freeDateChange;
    Boolean partialRefund;
    Boolean fullRefund;

    // Premium service benefits
    Boolean loungeAccess;
    Boolean airportTransfer;
}
