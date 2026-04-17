package com.airline.mapper;

import com.airline.dto.response.SeatInstanceResponse;
import com.airline.entity.SeatInstance;
import com.airline.enums.SeatAvailabilityStatus;

public class SeatInstanceMapper {

    public static SeatInstanceResponse toResponse(SeatInstance seatInstance) {
        if (seatInstance == null) {
            return null;
        }

        return SeatInstanceResponse
                .builder()
                .id(seatInstance.getId())
                .flightId(seatInstance.getFlightId())
                .seatId(seatInstance.getSeat() != null ? seatInstance.getSeat().getId() : null)
                .seatNumber(seatInstance.getSeat() != null ? seatInstance.getSeat().getSeatNumber() : null)
                .seatType(seatInstance.getSeat() != null ? seatInstance.getSeat().getSeatType().name() : null)
                .seatPosition(seatInstance.getSeat() != null ? seatInstance.getSeat().getFullPosition() : null)
                .seat(SeatMapper.toResponse(seatInstance.getSeat()))
                .status(seatInstance.getStatus())
                .flightInstanceId(seatInstance.getFlightInstanceId())
                .flightCabinId(seatInstance.getFlightInstanceCabin() != null ? seatInstance.getFlightInstanceCabin().getId() : null)
                .fare(seatInstance.getFare())
                .price(seatInstance.getPremiumSuperCharge())
                .version(seatInstance.getVersion())
                .createdAt(seatInstance.getCreatedAt())
                .updatedAt(seatInstance.getUpdatedAt())
                .isAvailable(seatInstance.getIsAvailable())
                .isBooked(seatInstance.getIsBooked())
                .isOccupied(seatInstance.getStatus() == SeatAvailabilityStatus.OCCUPIED)
//                .seatCharacteristics()
                .build();}
}
