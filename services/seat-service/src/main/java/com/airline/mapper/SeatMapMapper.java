package com.airline.mapper;

import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.SeatMapResponse;
import com.airline.entity.CabinClass;
import com.airline.entity.SeatMap;

public class SeatMapMapper {

    private SeatMapMapper() {
    }

    public static SeatMap toEntity(SeatMapRequest request, CabinClass cabinClass) {
        if (request == null) {
            return null;
        }

        return SeatMap.builder()
                .name(request.getName())
                .totalRows(request.getTotalRows())
                .leftSeatsPerRow(request.getLeftSeatsPerRow())
                .rightSeatsPerRow(request.getRightSeatsPerRow())
                .airlineId(request.getAirlineId())
                .cabinClass(cabinClass)
                .build();
    }

    public static void updateEntityFromRequest(SeatMap seatMap, SeatMapRequest request, CabinClass cabinClass) {
        if (seatMap == null || request == null) {
            return;
        }

        if (request.getName() != null) seatMap.setName(request.getName());
        if (request.getTotalRows() != null) seatMap.setTotalRows(request.getTotalRows());
        if (request.getLeftSeatsPerRow() != null) seatMap.setLeftSeatsPerRow(request.getLeftSeatsPerRow());
        if (request.getRightSeatsPerRow() != null) seatMap.setRightSeatsPerRow(request.getRightSeatsPerRow());
        if (request.getAirlineId() != null) seatMap.setAirlineId(request.getAirlineId());

        seatMap.setCabinClass(cabinClass);
    }

    public static SeatMapResponse toResponse(SeatMap seatMap) {
        if (seatMap == null) {
            return null;
        }

        CabinClass cabinClass = seatMap.getCabinClass();
        int totalSeats = calculateTotalSeats(seatMap);

        return SeatMapResponse.builder()
                .id(seatMap.getId())
                .name(seatMap.getName())
                .totalRows(seatMap.getTotalRows())
                .airlineId(seatMap.getAirlineId())
                .cabinClassId(cabinClass != null ? cabinClass.getId() : null)
                .cabinClassName(cabinClass != null && cabinClass.getName() != null ? cabinClass.getName().name() : null)
                .cabinClassCode(cabinClass != null ? cabinClass.getCode() : null)
                .totalSeats(totalSeats)
                .availableSeats(totalSeats)
                .occupiedSeats(0)
                .leftSeatsPerRow(seatMap.getLeftSeatsPerRow())
                .rightSeatsPerRow(seatMap.getRightSeatsPerRow())
                .build();
    }

    private static int calculateTotalSeats(SeatMap seatMap) {
        if (seatMap.getTotalRows() == null || seatMap.getLeftSeatsPerRow() == null || seatMap.getRightSeatsPerRow() == null) {
            return 0;
        }

        return seatMap.getTotalRows() * (seatMap.getLeftSeatsPerRow() + seatMap.getRightSeatsPerRow());
    }


    public static SeatMapResponse toSimpleResponse(SeatMap seatMap) {
        return SeatMapResponse.builder()
                .id(seatMap.getId())
                .totalRows(seatMap.getTotalRows())
                .leftSeatsPerRow(seatMap.getLeftSeatsPerRow())
                .rightSeatsPerRow(seatMap.getRightSeatsPerRow())
                .build();
    }
}
