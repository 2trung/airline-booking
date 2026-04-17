package com.airline.mapper;

import com.airline.dto.response.FlightInstanceCabinResponse;
import com.airline.entity.FlightInstanceCabin;
import com.airline.entity.SeatMap;

public class FlightInstanceCabinMapper {
    public static FlightInstanceCabinResponse toResponse(FlightInstanceCabin flightInstanceCabin) {
        if (flightInstanceCabin == null) {
            return null;
        }
        return FlightInstanceCabinResponse
                .builder()
                .id(flightInstanceCabin.getId())
                .flightInstanceId(flightInstanceCabin.getFlightInstanceId())
                .cabinClassType(flightInstanceCabin.getCabinClass().getName())
                .cabinClass(CabinClassMapper.toResponse(flightInstanceCabin.getCabinClass(), flightInstanceCabin.getCabinClass().getSeatMap()))
                .seats(
                        flightInstanceCabin.getSeatInstances().stream()
                                .map(SeatInstanceMapper::toResponse)
                                .toList()
                )
                .seatMap(flightInstanceCabin.getCabinClass() != null &&
                        flightInstanceCabin.getCabinClass().getSeatMap() != null ?
                        SeatMapMapper.toSimpleResponse(flightInstanceCabin.getCabinClass().getSeatMap())
                        : null)
                .totalSeats(flightInstanceCabin.getTotalSeats())
                .bookedSeats(flightInstanceCabin.getBookedSeats())
                .availableSeats(flightInstanceCabin.getAvailableSeats())
                .build();
    }
}
