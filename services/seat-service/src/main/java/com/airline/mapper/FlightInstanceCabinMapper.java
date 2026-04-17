package com.airline.mapper;

import com.airline.dto.response.FlightInstanceCabinResponse;
import com.airline.entity.FlightInstanceCabin;

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
                .cabinClass(CabinClassMapper.toResponse(flightInstanceCabin.getCabinClass()))
                //todo: set seat instance
//                .seats()
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
