package com.airline.dto.response;

import com.airline.enums.CabinClassType;
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
public class FareResponse {
    Long id;
    String name;
    Character rbdCode;

    Long flightId;
    Long cabinClassId;
    CabinClassType cabinClassType;

    Double baseFare;
    Double taxesAndFees;
    Double airlineFees;
    Double totalPrice;
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

    Long fareRulesId;
    FareRulesResponse fareRules;
    BaggagePolicyResponse baggagePolicy;

    Instant createdAt;
    Instant updatedAt;
}
