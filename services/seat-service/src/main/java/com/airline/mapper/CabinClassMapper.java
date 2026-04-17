package com.airline.mapper;

import com.airline.dto.request.CabinClassRequest;
import com.airline.dto.response.CabinClassResponse;
import com.airline.entity.CabinClass;

import java.time.Instant;

public class CabinClassMapper {

    private CabinClassMapper() {
    }

    public static CabinClass toEntity(CabinClassRequest request) {
        if (request == null) {
            return null;
        }

        return CabinClass.builder()
                .name(request.getName())
                .code(normalizeCode(request.getCode()))
                .description(request.getDescription())
                .airCraftId(request.getAircraftId())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isBookable(request.getIsBookable() != null ? request.getIsBookable() : true)
                .typicalSeatPitch(request.getTypicalSeatPitch())
                .typicalSeatWidth(request.getTypicalSeatWidth())
                .seatType(request.getSeatType())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static void updateEntityFromRequest(CabinClass cabinClass, CabinClassRequest request) {
        if (cabinClass == null || request == null) {
            return;
        }
        if (request.getName() != null) cabinClass.setName(request.getName());
        if (request.getCode() != null) cabinClass.setCode(request.getCode());
        if (request.getDescription() != null) cabinClass.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) cabinClass.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) cabinClass.setIsActive(request.getIsActive());
        if (request.getIsBookable() != null) cabinClass.setIsBookable(request.getIsBookable());
        if (request.getTypicalSeatPitch() != null) cabinClass.setTypicalSeatPitch(request.getTypicalSeatPitch());
        if (request.getTypicalSeatWidth() != null) cabinClass.setTypicalSeatWidth(request.getTypicalSeatWidth());
        if (request.getSeatType() != null) cabinClass.setSeatType(request.getSeatType());


    }

    public static CabinClassResponse toResponse(CabinClass cabinClass) {
        if (cabinClass == null) {
            return null;
        }

        return CabinClassResponse.builder()
                .id(cabinClass.getId())
                .name(cabinClass.getName() != null ? cabinClass.getName().name() : null)
                .code(cabinClass.getCode())
                .description(cabinClass.getDescription())
                .aircraftId(cabinClass.getAirCraftId())
                .displayOrder(cabinClass.getDisplayOrder())
                .isActive(cabinClass.getIsActive())
                .isBookable(cabinClass.getIsBookable())
                .typicalSeatPitch(cabinClass.getTypicalSeatPitch())
                .typicalSeatWidth(cabinClass.getTypicalSeatWidth())
                .seatType(cabinClass.getSeatType())
                .createdAt(cabinClass.getCreatedAt())
                .updatedAt(cabinClass.getUpdatedAt())
                .build();
    }

    private static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}
