package com.airline.mapper;

import com.airline.dto.request.FareRequest;
import com.airline.dto.response.FareResponse;
import com.airline.embeddable.*;
import com.airline.entity.Fare;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FareMapper {
    private final FareRulesMapper fareRulesMapper;
    private final BaggagePolicyMapper baggagePolicyMapper;


    public Fare toEntity(FareRequest request) {
        if (request == null) {
            return null;
        }

        return Fare.builder().name(request.getName()).rbdCode(request.getRbdCode()).flightId(request.getFlightId()).cabinClassId(request.getCabinClassId()).baseFare(request.getBaseFare()).taxesAndFees(request.getTaxesAndFees() != null ? request.getTaxesAndFees() : 0.0).airlineFees(request.getAirlineFees() != null ? request.getAirlineFees() : 0.0).currentPrice(calculateTotalFare(request)).totalPrice(calculateTotalFare(request)).fareLabel(request.getFareLabel()).seatBenefits(mapSeatBenefits(request)).boardingBenefits(mapBoardingBenefits(request)).inFlightBenefits(mapInFlightBenefits(request)).flexibilityBenefits(mapFlexibilityBenefits(request)).premiumServiceBenefits(mapPremiumServiceBenefits(request)).build();
    }

    public FareResponse toResponse(Fare fare) {
        if (fare == null) {
            return null;
        }

        return FareResponse.builder()
                .id(fare.getId())
                .name(fare.getName())
                .rbdCode(fare.getRbdCode())
                .flightId(fare.getFlightId())
                .cabinClassId(fare.getCabinClassId())
                .cabinClassType(fare.getCabinClassType())
                .baseFare(fare.getBaseFare())
                .taxesAndFees(fare.getTaxesAndFees())
                .airlineFees(fare.getAirlineFees())
                .totalPrice(fare.getTotalPrice())
                .fareLabel(fare.getFareLabel())
                .extraSeatSpace(fare.getSeatBenefits() != null ? fare.getSeatBenefits().getExtraSeatSpace() : null)
                .preferredSeatChoice(fare.getSeatBenefits() != null ? fare.getSeatBenefits().getPreferredSeatChoice() : null)
                .advanceSeatSelection(fare.getSeatBenefits() != null ? fare.getSeatBenefits().getAdvanceSeatSelection() : null)
                .guaranteedSeatTogether(fare.getSeatBenefits() != null ? fare.getSeatBenefits().getGuaranteedSeatTogether() : null)
                .priorityBoarding(fare.getBoardingBenefits() != null ? fare.getBoardingBenefits().getPriorityBoarding() : null)
                .priorityCheckIn(fare.getBoardingBenefits() != null ? fare.getBoardingBenefits().getPriorityCheckIn() : null)
                .prioritySecurity(fare.getBoardingBenefits() != null ? fare.getBoardingBenefits().getPrioritySecurity() : null)
                .complimentaryMeals(fare.getInFlightBenefits() != null ? fare.getInFlightBenefits().getComplimentaryMeals() : null)
                .premiumMealChoice(fare.getInFlightBenefits() != null ? fare.getInFlightBenefits().getPremiumMealChoice() : null)
                .inFlightInternet(fare.getInFlightBenefits() != null ? fare.getInFlightBenefits().getInFlightInternet() : null)
                .inFlightEntertainment(fare.getInFlightBenefits() != null ? fare.getInFlightBenefits().getInFlightEntertainment() : null)
                .complimentaryBeverages(fare.getInFlightBenefits() != null ? fare.getInFlightBenefits().getComplimentaryBeverages() : null)
                .freeDateChange(fare.getFlexibilityBenefits() != null ? fare.getFlexibilityBenefits().getFreeDateChange() : null)
                .partialRefund(fare.getFlexibilityBenefits() != null ? fare.getFlexibilityBenefits().getPartialRefund() : null)
                .fullRefund(fare.getFlexibilityBenefits() != null ? fare.getFlexibilityBenefits().getFullRefund() : null)
                .loungeAccess(fare.getPremiumServiceBenefits() != null ? fare.getPremiumServiceBenefits().getLoungeAccess() : null)
                .airportTransfer(fare.getPremiumServiceBenefits() != null ? fare.getPremiumServiceBenefits().getAirportTransfer() : null)
                .fareRules(fare.getFareRules() != null ? fareRulesMapper.toResponse(fare.getFareRules()) : null)
                .baggagePolicy(fare.getBaggagePolicy() != null ? baggagePolicyMapper.toResponse(fare.getBaggagePolicy()) : null)
                .createdAt(fare.getCreatedAt()).updatedAt(fare.getUpdatedAt()).build();
    }

    public void updateEntity(Fare fare, FareRequest request) {
        if (fare == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            fare.setName(request.getName());
        }

        if (request.getRbdCode() != null) {
            fare.setRbdCode(request.getRbdCode());
        }

        if (request.getFlightId() != null) {
            fare.setFlightId(request.getFlightId());
        }

        if (request.getCabinClassId() != null) {
            fare.setCabinClassId(request.getCabinClassId());
        }

        if (request.getBaseFare() != null) {
            fare.setBaseFare(request.getBaseFare());
        }
        if (request.getTaxesAndFees() != null) {
            fare.setTaxesAndFees(request.getTaxesAndFees());
        }

        if (request.getAirlineFees() != null) {
            fare.setAirlineFees(request.getAirlineFees());
        }

        if (request.getCurrentPrice() != null) {
            fare.setCurrentPrice(request.getCurrentPrice());
        }

        if (request.getFareLabel() != null) {
            fare.setFareLabel(request.getFareLabel());
        }

        SeatBenefits sb = fare.getSeatBenefits();
        if (sb == null) {
            sb = new SeatBenefits();
            fare.setSeatBenefits(sb);
        }
        if (request.getExtraSeatSpace() != null) {
            sb.setExtraSeatSpace(request.getExtraSeatSpace());
        }
        if (request.getPreferredSeatChoice() != null) {
            sb.setPreferredSeatChoice(request.getPreferredSeatChoice());
        }
        if (request.getAdvanceSeatSelection() != null) {
            sb.setAdvanceSeatSelection(request.getAdvanceSeatSelection());
        }
        if (request.getGuaranteedSeatTogether() != null) {
            sb.setGuaranteedSeatTogether(request.getGuaranteedSeatTogether());
        }

        BoardingBenefits bb = fare.getBoardingBenefits();
        if (bb == null) {
            bb = new BoardingBenefits();
            fare.setBoardingBenefits(bb);
        }
        if (request.getPriorityBoarding() != null) {
            bb.setPriorityBoarding(request.getPriorityBoarding());
        }
        if (request.getPriorityCheckIn() != null) {
            bb.setPriorityCheckIn(request.getPriorityCheckIn());
        }

        if (request.getPrioritySecurity() != null) {
            bb.setPrioritySecurity(request.getPrioritySecurity());
        }

        InFlightBenefits ifb = fare.getInFlightBenefits();
        if (ifb == null) {
            ifb = new InFlightBenefits();
            fare.setInFlightBenefits(ifb);
        }
        if (request.getComplimentaryMeals() != null) {
            ifb.setComplimentaryMeals(request.getComplimentaryMeals());
        }
        if (request.getPremiumMealChoice() != null) {
            ifb.setPremiumMealChoice(request.getPremiumMealChoice());
        }
        if (request.getInFlightInternet() != null) {
            ifb.setInFlightInternet(request.getInFlightInternet());
        }
        if (request.getInFlightEntertainment() != null) {
            ifb.setInFlightEntertainment(request.getInFlightEntertainment());
        }
        if (request.getComplimentaryBeverages() != null) {
            ifb.setComplimentaryBeverages(request.getComplimentaryBeverages());
        }

        FlexibilityBenefits fb = fare.getFlexibilityBenefits();
        if (fb == null) {
            fb = new FlexibilityBenefits();
            fare.setFlexibilityBenefits(fb);
        }
        if (request.getFreeDateChange() != null) {
            fb.setFreeDateChange(request.getFreeDateChange());
        }
        if (request.getPartialRefund() != null) {
            fb.setPartialRefund(request.getPartialRefund());
        }
        if (request.getFullRefund() != null) {
            fb.setFullRefund(request.getFullRefund());
        }

        PremiumServiceBenefits ps = fare.getPremiumServiceBenefits();
        if (ps == null) {
            ps = new PremiumServiceBenefits();
            fare.setPremiumServiceBenefits(ps);
        }
        if (request.getLoungeAccess() != null) {
            ps.setLoungeAccess(request.getLoungeAccess());
        }
        if (request.getAirportTransfer() != null) {
            ps.setAirportTransfer(request.getAirportTransfer());
        }


    }

    private Double calculateTotalFare(FareRequest request) {
        double base = request.getCurrentPrice() != null ? request.getCurrentPrice() : request.getBaseFare();
        double taxes = request.getTaxesAndFees() != null ? request.getTaxesAndFees() : 0.0;
        double fees = request.getAirlineFees() != null ? request.getAirlineFees() : 0.0;
        return base + taxes + fees;
    }

    private SeatBenefits mapSeatBenefits(FareRequest request) {
        SeatBenefits benefits = new SeatBenefits();
        benefits.setExtraSeatSpace(request.getExtraSeatSpace() != null ? request.getExtraSeatSpace() : false);
        benefits.setPreferredSeatChoice(request.getPreferredSeatChoice() != null ? request.getPreferredSeatChoice() : false);
        benefits.setAdvanceSeatSelection(request.getAdvanceSeatSelection() != null ? request.getAdvanceSeatSelection() : false);
        benefits.setGuaranteedSeatTogether(request.getGuaranteedSeatTogether() != null ? request.getGuaranteedSeatTogether() : false);
        return benefits;
    }

    private BoardingBenefits mapBoardingBenefits(FareRequest request) {
        BoardingBenefits benefits = new BoardingBenefits();
        benefits.setPriorityBoarding(request.getPriorityBoarding() != null ? request.getPriorityBoarding() : false);
        benefits.setPriorityCheckIn(request.getPriorityCheckIn() != null ? request.getPriorityCheckIn() : false);
        benefits.setPrioritySecurity(request.getPrioritySecurity() != null ? request.getPrioritySecurity() : false);
        return benefits;
    }

    private InFlightBenefits mapInFlightBenefits(FareRequest request) {
        InFlightBenefits benefits = new InFlightBenefits();
        benefits.setComplimentaryMeals(request.getComplimentaryMeals() != null ? request.getComplimentaryMeals() : false);
        benefits.setPremiumMealChoice(request.getPremiumMealChoice() != null ? request.getPremiumMealChoice() : false);
        benefits.setInFlightInternet(request.getInFlightInternet() != null ? request.getInFlightInternet() : false);
        benefits.setInFlightEntertainment(request.getInFlightEntertainment() != null ? request.getInFlightEntertainment() : false);
        benefits.setComplimentaryBeverages(request.getComplimentaryBeverages() != null ? request.getComplimentaryBeverages() : false);
        return benefits;
    }

    private FlexibilityBenefits mapFlexibilityBenefits(FareRequest request) {
        FlexibilityBenefits benefits = new FlexibilityBenefits();
        benefits.setFreeDateChange(request.getFreeDateChange() != null ? request.getFreeDateChange() : false);
        benefits.setPartialRefund(request.getPartialRefund() != null ? request.getPartialRefund() : false);
        benefits.setFullRefund(request.getFullRefund() != null ? request.getFullRefund() : false);
        return benefits;
    }

    private PremiumServiceBenefits mapPremiumServiceBenefits(FareRequest request) {
        PremiumServiceBenefits benefits = new PremiumServiceBenefits();
        benefits.setLoungeAccess(request.getLoungeAccess() != null ? request.getLoungeAccess() : false);
        benefits.setAirportTransfer(request.getAirportTransfer() != null ? request.getAirportTransfer() : false);
        return benefits;
    }
}
