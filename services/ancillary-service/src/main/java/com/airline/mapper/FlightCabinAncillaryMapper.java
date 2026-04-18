package com.airline.mapper;

import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.entity.FlightCabinAncillary;

import java.util.List;

public class FlightCabinAncillaryMapper {

    public static FlightCabinAncillaryResponse toResponse(FlightCabinAncillary flightCabinAncillary,
                                                          List<InsuranceCoverageResponse> insuranceCoverages
    ) {
        if (flightCabinAncillary == null) {
            return null;
        }

        return FlightCabinAncillaryResponse.builder()
                .id(flightCabinAncillary.getId())
                .flightId(flightCabinAncillary.getFlightId())
                .cabinClassId(flightCabinAncillary.getCabinClassId())
                .ancillary(AncillaryMapper.toResponse(flightCabinAncillary.getAncillary(), insuranceCoverages))
                .available(flightCabinAncillary.getAvailable())
                .maxQuantity(flightCabinAncillary.getMaxQuantity())
                .price(flightCabinAncillary.getPrice())
                .includedInFare(flightCabinAncillary.getIncludedInFare())
                .build();
    }
}

