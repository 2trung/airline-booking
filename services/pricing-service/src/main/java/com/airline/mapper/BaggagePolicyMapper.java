package com.airline.mapper;

import com.airline.dto.request.BaggagePolicyRequest;
import com.airline.dto.response.BaggagePolicyResponse;
import com.airline.entity.BaggagePolicy;
import com.airline.entity.Fare;
import org.springframework.stereotype.Component;

@Component
public class BaggagePolicyMapper {

    public BaggagePolicy toEntity(BaggagePolicyRequest request, Fare fare) {
        if (request == null) {
            return null;
        }

        return BaggagePolicy.builder()
                .name(request.getName())
                .description(request.getDescription())
                .fare(fare)
                .flightId(fare != null ? fare.getFlightId() : null)
                .airlineId(request.getAirlineId())
                .cabinBaggageMaxWeight(request.getCabinBaggageMaxWeight())
                .cabinBaggageMaxDimension(request.getCabinBaggageMaxDimension() != null
                        ? request.getCabinBaggageMaxDimension().intValue() : null)
                .cabinBaggagePieces(request.getCabinBaggagePieces() != null ? request.getCabinBaggagePieces() : 1)
                .checkInBaggageMaxWeight(request.getCheckInBaggageMaxWeight())
                .checkInBaggagePieces(request.getCheckInBaggagePieces() != null ? request.getCheckInBaggagePieces() : 1)
                .checkInBaggageWeightPerPiece(request.getCheckInBaggageWeightPerPiece())
                .freeCheckedBagsAllowed(request.getFreeCheckedBagsAllowed() != null ? request.getFreeCheckedBagsAllowed() : 0)
                .priorityBaggageAllowed(request.getPriorityBaggageAllowed() != null ? request.getPriorityBaggageAllowed() : false)
                .extraBaggageAllowed(request.getExtraBaggageAllowed() != null ? request.getExtraBaggageAllowed() : false)
                .build();
    }

    public BaggagePolicyResponse toResponse(BaggagePolicy baggagePolicy) {
        if (baggagePolicy == null) {
            return null;
        }

        return BaggagePolicyResponse.builder()
                .id(baggagePolicy.getId())
                .name(baggagePolicy.getName())
                .description(baggagePolicy.getDescription())
                .flightId(baggagePolicy.getFlightId())
                .airlineId(baggagePolicy.getAirlineId())
                .cabinBaggageMaxWeight(baggagePolicy.getCabinBaggageMaxWeight())
                .cabinBaggageMaxDimension(baggagePolicy.getCabinBaggageMaxDimension())
                .cabinBaggagePieces(baggagePolicy.getCabinBaggagePieces())
                .cabinBaggageMaxDimension(baggagePolicy.getCabinBaggageMaxDimension())
                .checkInBaggageMaxWeight(baggagePolicy.getCheckInBaggageMaxWeight())
                .checkInBaggagePieces(baggagePolicy.getCheckInBaggagePieces())
                .checkInBaggageWeightPerPiece(baggagePolicy.getCheckInBaggageWeightPerPiece())
                .freeCheckedBagsAllowed(baggagePolicy.getFreeCheckedBagsAllowed())
                .priorityBaggageAllowed(baggagePolicy.getPriorityBaggageAllowed())
                .extraBaggageAllowed(baggagePolicy.getExtraBaggageAllowed())
                .createdAt(baggagePolicy.getCreatedAt())
                .updatedAt(baggagePolicy.getUpdatedAt())
                .build();
    }

    public void updateEntity(BaggagePolicy baggagePolicy, BaggagePolicyRequest request) {
        if (baggagePolicy == null || request == null) {
            return;
        }

        if (request.getName() != null) {
            baggagePolicy.setName(request.getName());
        }
        if (request.getDescription() != null) {
            baggagePolicy.setDescription(request.getDescription());
        }
        if (request.getAirlineId() != null) {
            baggagePolicy.setAirlineId(request.getAirlineId());
        }
        if (request.getCabinBaggageMaxWeight() != null) {
            baggagePolicy.setCabinBaggageMaxWeight(request.getCabinBaggageMaxWeight());
        }
        if (request.getCabinBaggageMaxDimension() != null) {
            baggagePolicy.setCabinBaggageMaxDimension(request.getCabinBaggageMaxDimension().intValue());
        }
        if (request.getCabinBaggagePieces() != null) {
            baggagePolicy.setCabinBaggagePieces(request.getCabinBaggagePieces());
        }
        if (request.getCheckInBaggageMaxWeight() != null) {
            baggagePolicy.setCheckInBaggageMaxWeight(request.getCheckInBaggageMaxWeight());
        }
        if (request.getCheckInBaggagePieces() != null) {
            baggagePolicy.setCheckInBaggagePieces(request.getCheckInBaggagePieces());
        }
        if (request.getCheckInBaggageWeightPerPiece() != null) {
            baggagePolicy.setCheckInBaggageWeightPerPiece(request.getCheckInBaggageWeightPerPiece());
        }
        if (request.getFreeCheckedBagsAllowed() != null) {
            baggagePolicy.setFreeCheckedBagsAllowed(request.getFreeCheckedBagsAllowed());
        }
        if (request.getPriorityBaggageAllowed() != null) {
            baggagePolicy.setPriorityBaggageAllowed(request.getPriorityBaggageAllowed());
        }
        if (request.getExtraBaggageAllowed() != null) {
            baggagePolicy.setExtraBaggageAllowed(request.getExtraBaggageAllowed());
        }
    }
}
