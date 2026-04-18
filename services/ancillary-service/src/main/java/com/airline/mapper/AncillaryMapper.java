package com.airline.mapper;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.entity.Ancillary;

import java.util.List;

public class AncillaryMapper {
    public static AncillaryResponse toResponse(Ancillary ancillary, List<InsuranceCoverageResponse> coverages) {
        if (ancillary == null) return null;

        return AncillaryResponse
                .builder()
                .id(ancillary.getId())
                .type(ancillary.getType())
                .subType(ancillary.getSubType())
                .name(ancillary.getName())
                .rfisc(ancillary.getRfisc())
                .displayOrder(ancillary.getDisplayOrder())
                .metadata(ancillary.getMetadata())
                .description(ancillary.getDescription())
                .coverages(coverages)
                .airlineId(ancillary.getAirlineId())
                .build();

    }
}
