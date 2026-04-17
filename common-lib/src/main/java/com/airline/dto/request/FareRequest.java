package com.airline.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class FareRequest {

    @NotNull(message = "Fare name is required")
    String name;

    @NotNull(message = "RBD code is required")
    Character rbdCode;

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Cabin class ID is required")
    Long cabinClassId;

    @NotNull(message = "Base fare is required")
    Double baseFare;

    Double taxesAndFees;
    Double airlineFees;
    Double currentPrice;

    @NotNull(message = "Fare label is required")
    String fareLabel;

    Boolean extraSeatSpace;
    Boolean preferredSeatChoice;
    Boolean advanceSeatSelection;
    Boolean guaranteedSeatTogether;

    Boolean priorityBoarding;
    Boolean priorityCheckIn;
    Boolean prioritySecurity;

    Boolean complimentaryMeals;
    Boolean premiumMealChoice;
    Boolean inFlightInternet;
    Boolean inFlightEntertainment;
    Boolean complimentaryBeverages;

    Boolean freeDateChange;
    Boolean partialRefund;
    Boolean fullRefund;

    Boolean loungeAccess;
    Boolean airportTransfer;
}
