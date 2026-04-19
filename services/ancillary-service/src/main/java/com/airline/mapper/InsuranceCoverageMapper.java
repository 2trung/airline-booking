package com.airline.mapper;

import com.airline.dto.request.InsuranceCoverageRequest;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.entity.Ancillary;
import com.airline.entity.InsuranceCoverage;

public class InsuranceCoverageMapper {

    public static InsuranceCoverageResponse toResponse(InsuranceCoverage insuranceCoverage) {

        if (insuranceCoverage == null) {
            return null;
        }


        return InsuranceCoverageResponse
                .builder()
                .id(insuranceCoverage.getId())
                .ancillaryId(insuranceCoverage.getAncillary().getId())
                .ancillaryName(insuranceCoverage.getAncillary().getName())
                .coverageType(insuranceCoverage.getCoverageType())
                .name(insuranceCoverage.getName())
                .description(insuranceCoverage.getDescription())
                .coverageAmount(insuranceCoverage.getCoverageAmount())
                .isFlat(insuranceCoverage.getIsFlat())
                .claimCondition(insuranceCoverage.getClaimCondition())
                .emergencyContact(insuranceCoverage.getEmergencyContact())
                .displayOrder(insuranceCoverage.getDisplayOrder())
                .active(insuranceCoverage.getActive())
                .build();
    }


    public static InsuranceCoverage toEntity(InsuranceCoverageRequest request, Ancillary ancillary) {

        if (request == null) {
            return null;
        }

        return InsuranceCoverage
                .builder()
                .ancillary(ancillary)
                .coverageType(request.getCoverageType())
                .name(request.getName())
                .description(request.getDescription())
                .coverageAmount(request.getCoverageAmount())
                .isFlat(request.getIsFlat() != null ? request.getIsFlat() : true)
                .claimCondition(request.getClaimCondition())
                .claimCondition(request.getClaimCondition())
                .emergencyContact(request.getEmergencyContact())
                .displayOrder(request.getDisplayOrder())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public static void updateEntityFromRequest(InsuranceCoverage entity, InsuranceCoverageRequest request, Ancillary ancillary) {
        if (entity == null || request == null) {
            return;
        }

        if (ancillary != null) {
            entity.setAncillary(ancillary);
        }
        if (request.getCoverageType() != null) {
            entity.setCoverageType(request.getCoverageType());
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
        if (request.getCoverageAmount() != null) {
            entity.setCoverageAmount(request.getCoverageAmount());
        }
        if (request.getIsFlat() != null) {
            entity.setIsFlat(request.getIsFlat());
        }
        if (request.getClaimCondition() != null) {
            entity.setClaimCondition(request.getClaimCondition());
        }
        if (request.getEmergencyContact() != null) {
            entity.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getDisplayOrder() != null) {
            entity.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getActive() != null) {
            entity.setActive(request.getActive());
        }
    }
}
