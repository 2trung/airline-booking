package com.airline.mapper;

import com.airline.dto.request.FlightRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightResponse;
import com.airline.entity.Flight;
import com.airline.enums.FlightStatus;

public class FlightMapper {

    private FlightMapper() {
    }

    public static Flight toEntity(FlightRequest request) {
        if (request == null) {
            return null;
        }

        return Flight.builder()
                .flightNumber(normalizeFlightNumber(request.getFlightNumber()))
                .airlineId(request.getAirlineId())
                .aircraftId(request.getAircraftId())
                .departureAirportId(request.getDepartureAirportId())
                .arrivalAirportId(request.getArrivalAirportId())
                .status(request.getStatus() == null ? FlightStatus.SCHEDULED : request.getStatus())
                .build();
    }

    public static void updateEntityFromRequest(Flight flight, FlightRequest request) {
        if (flight == null || request == null) {
            return;
        }

        flight.setFlightNumber(normalizeFlightNumber(request.getFlightNumber()));
        flight.setAirlineId(request.getAirlineId());
        flight.setAircraftId(request.getAircraftId());
        flight.setDepartureAirportId(request.getDepartureAirportId());
        flight.setArrivalAirportId(request.getArrivalAirportId());
        if (request.getStatus() != null) {
            flight.setStatus(request.getStatus());
        }
    }

    public static FlightResponse toResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(AirlineResponse.builder().id(flight.getAirlineId()).build())
                .aircraft(AircraftResponse.builder().id(flight.getAircraftId()).build())
                .departureAirport(AirportResponse.builder().id(flight.getDepartureAirportId()).build())
                .arrivalAirport(AirportResponse.builder().id(flight.getArrivalAirportId()).build())
                .status(flight.getStatus())
                .lowestPrice(flight.getLowestPrice())
                .totalAvailableSeats(flight.getTotalAvailableSeats())
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .build();
    }

    private static String normalizeFlightNumber(String flightNumber) {
        return flightNumber == null ? null : flightNumber.trim().toUpperCase();
    }
}
