package com.airline.dto.response;

import com.airline.enums.CabinClassType;
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
public class FareResponse {

    Long id;
    String name;
    Character rbdCode;
    Long flightId;
    Long cabinClassId;
    CabinClassType cabinClass;

    // Pricing
    Double baseFare;
    Double taxesAndFees;
    Double airlineFees;
    Double currentPrice;
    Double totalPrice;
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

    // Relationships
    Long fareRulesId;
    FareRulesResponse fareRules;
    BaggagePolicyResponse baggagePolicy;

    // Audit
    Instant createdAt;
    Instant updatedAt;
}
