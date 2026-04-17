package com.airline.mapper;

import com.airline.dto.request.FareRulesRequest;
import com.airline.dto.response.FareRulesResponse;
import com.airline.entity.Fare;
import com.airline.entity.FareRules;
import org.springframework.stereotype.Component;

@Component
public class FareRulesMapper {

    public FareRules toEntity(FareRulesRequest request) {
        if (request == null) {
            return null;
        }

        return FareRules.builder()
                .ruleName(request.getRuleName())
                .airlineId(request.getAirlineId())
                .isRefundable(request.getIsRefundable() != null ? request.getIsRefundable() : false)
                .isChangeable(request.getIsChangeable() != null ? request.getIsChangeable() : false)
                .changeFee(request.getChangeFee())
                .cancellationFee(request.getCancellationFee())
                .refundableDays(request.getRefundableDays())
                .changeableHours(request.getChangeableHours())
                .build();
    }

    public FareRules toEntity(FareRulesRequest request, Fare fare) {
        if (request == null) {
            return null;
        }

        return FareRules.builder()
                .ruleName(request.getRuleName())
                .airlineId(request.getAirlineId())
                .isRefundable(request.getIsRefundable() != null ? request.getIsRefundable() : false)
                .isChangeable(request.getIsChangeable() != null ? request.getIsChangeable() : false)
                .changeFee(request.getChangeFee())
                .cancellationFee(request.getCancellationFee())
                .refundableDays(request.getRefundableDays())
                .changeableHours(request.getChangeableHours())
                .fare(fare)
                .build();

    }

    public FareRulesResponse toResponse(FareRules fareRules) {
        if (fareRules == null) {
            return null;
        }

        return FareRulesResponse.builder()
                .id(fareRules.getId())
                .ruleName(fareRules.getRuleName())
                .fareId(fareRules.getFare() != null ? fareRules.getFare().getId() : null)
                .airlineId(fareRules.getAirlineId())
                .isRefundable(fareRules.getIsRefundable())
                .isChangeable(fareRules.getIsChangeable())
                .changeFee(fareRules.getChangeFee())
                .cancellationFee(fareRules.getCancellationFee())
                .refundableDays(fareRules.getRefundableDays())
                .changeableHours(fareRules.getChangeableHours())
                .createdAt(fareRules.getCreatedAt())
                .updatedAt(fareRules.getUpdatedAt())
                .build();
    }

    public void updateEntity(FareRules fareRules, FareRulesRequest request) {
        if (fareRules == null || request == null) {
            return;
        }

        if (request.getRuleName() != null) {
            fareRules.setRuleName(request.getRuleName());
        }

        if (request.getAirlineId() != null) {
            fareRules.setAirlineId(request.getAirlineId());
        }

        if (request.getIsRefundable() != null) {
            fareRules.setIsRefundable(request.getIsRefundable());
        }

        if (request.getIsChangeable() != null) {
            fareRules.setIsChangeable(request.getIsChangeable());
        }

        if (request.getChangeFee() != null) {
            fareRules.setChangeFee(request.getChangeFee());
        }

        if (request.getCancellationFee() != null) {
            fareRules.setCancellationFee(request.getCancellationFee());
        }

        if (request.getRefundableDays() != null) {
            fareRules.setRefundableDays(request.getRefundableDays());
        }

        if (request.getChangeableHours() != null) {
            fareRules.setChangeableHours(request.getChangeableHours());
        }
    }
}
