package com.airline.mapper;

import com.airline.dto.response.SeatResponse;
import com.airline.entity.Seat;

public class SeatMapper {

    public static SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .seatRow(seat.getSeatRow())
                .columnLetter(seat.getColumnLetter())
                .seatType(seat.getSeatType())
                .isAvailable(seat.getIsAvailable())
                .isActive(seat.getIsActive())
                .isBlocked(seat.getIsBlocked())
                .isEmergencyExit(seat.getIsEmergencyExit())
                .hasExtraLegRoom(seat.getHasExtraLegRoom())
                .hasPowerOutlet(seat.getHasPowerOutlet())
                .hasTvScreen(seat.getHasTvScreen())
                .hasExtraWidth(seat.getHasExtraWidth())
                .seatPitch(seat.getSeatPitch())
                .seatWidth(seat.getSeatWidth())
                .reclineAngle(seat.getReclineAngle())
                .seatMapId(seat.getSeatMap() != null ? seat.getSeatMap().getId() : null)
                .seatMapName(seat.getSeatMap() != null ? seat.getSeatMap().getName() : null)
                .cabinClassName(seat.getCabinClass() != null ? seat.getCabinClass().getName().name() : null)
                .carbinClassId(seat.getCabinClass() != null ? seat.getCabinClass().getId() : null)
                .createdAt(seat.getCreatedAt())
                .updatedAt(seat.getUpdatedAt())
                .updatedBy(seat.getUpdatedBy())
                .createdBy(seat.getCreatedBy())
                .isBookable(seat.isBookable())
                .fullPosition(seat.getFullPosition())
                .build();
    }
}
