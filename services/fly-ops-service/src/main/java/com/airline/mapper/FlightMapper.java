package com.airline.mapper;

import com.airline.dto.request.FlightRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightResponse;
import com.airline.entity.Flight;
import com.airline.enums.FlightStatus;

public class FlightMapper {

    public static Flight toEntity(FlightRequest request) {
        if (request == null) return null;
        return Flight.builder()
                .flightNumber(request.getFlightNumber())
                .aircraftId(request.getAircraftId())
                .departureAirportId(request.getDepartureAirportId())
                .arrivalAirportId(request.getArrivalAirportId())
                .status(request.getStatus() != null ? request.getStatus() : FlightStatus.SCHEDULED)
                .build();
    }

    public static FlightResponse toResponse(Flight flight,
                                            AircraftResponse aircraft,
                                            AirlineResponse airlineResponse,
                                            AirportResponse departureAirport,
                                            AirportResponse arrivalAirport) {
        if (flight == null) return null;
        return FlightResponse.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(airlineResponse)
                .aircraft(aircraft)
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .status(flight.getStatus())
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .build();
    }

    public static void updateEntity(FlightRequest request, Flight existing) {
        if (request == null || existing == null) return;
        if (request.getFlightNumber() != null) existing.setFlightNumber(request.getFlightNumber());
        if (request.getAirlineId() != null) existing.setAirlineId(request.getAirlineId());
        if (request.getAircraftId() != null) existing.setAircraftId(request.getAircraftId());
        if (request.getDepartureAirportId() != null) existing.setDepartureAirportId(request.getDepartureAirportId());
        if (request.getArrivalAirportId() != null) existing.setArrivalAirportId(request.getArrivalAirportId());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
    }
}
