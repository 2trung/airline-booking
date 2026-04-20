package com.airline.mapper;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import com.airline.embeddable.Support;
import com.airline.entity.Airline;
import com.airline.enums.AirlineStatus;

public class AirlineMapper {

    private AirlineMapper() {
    }

    public static Airline toEntity(AirlineRequest request) {
        if (request == null) {
            return null;
        }

        return Airline.builder()
                .iataCode(normalizeCode(request.getIataCode()))
                .icaoCode(normalizeCode(request.getIcaoCode()))
                .name(request.getName())
                .alias(request.getAlias())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .status(request.getStatus() != null ? request.getStatus() : AirlineStatus.ACTIVE)
                .headquartersCityId(request.getHeadquartersCityId())
                .support(Support.builder()
                        .email(request.getSupportEmail())
                        .phone(request.getSupportPhone())
                        .hours(request.getSupportHours())
                        .build())
                .alliance(request.getAlliance())
                .build();
    }

    public static AirlineResponse toResponse(Airline airline) {
        if (airline == null) {
            return null;
        }

        return AirlineResponse.builder()
                .id(airline.getId())
                .name(airline.getName())
                .iataCode(airline.getIataCode())
                .icaoCode(airline.getIcaoCode())
                .alias(airline.getAlias())
                .logoUrl(airline.getLogoUrl())
                .status(airline.getStatus())
                .alliance(airline.getAlliance())
                .createdAt(airline.getCreatedAt())
                .updatedAt(airline.getUpdatedAt())
                .ownerId(airline.getOwnerId())
                .updatedBy(airline.getUpdatedById() == null ? null : String.valueOf(airline.getUpdatedById()))
                .support(airline.getSupport())
                .build();
    }

    public static AirlineDropdownItem toDropdownItem(Airline airline) {
        if (airline == null) {
            return null;
        }

        return AirlineDropdownItem.builder()
                .id(airline.getId())
                .name(airline.getName())
                .iataCode(airline.getIataCode())
                .icaoCode(airline.getIcaoCode())
                .logoUrl(airline.getLogoUrl())
                .build();
    }

    public static void updateEntityFromRequest(Airline airline, AirlineRequest request) {
        if (request == null || airline == null) {
            return;
        }

        airline.setIataCode(normalizeCode(request.getIataCode()));
        airline.setIcaoCode(normalizeCode(request.getIcaoCode()));
        airline.setName(request.getName());
        airline.setAlias(request.getAlias());
        airline.setLogoUrl(request.getLogoUrl());
        airline.setWebsite(request.getWebsite());
        airline.setStatus(request.getStatus() != null ? request.getStatus() : airline.getStatus());
        airline.setHeadquartersCityId(request.getHeadquartersCityId());
        airline.setAlliance(request.getAlliance());
        airline.setSupport(Support.builder()
                .email(request.getSupportEmail())
                .phone(request.getSupportPhone())
                .hours(request.getSupportHours())
                .build());
    }

    private static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}

