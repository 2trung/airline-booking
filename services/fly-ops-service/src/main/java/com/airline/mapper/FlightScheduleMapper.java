package com.airline.mapper;

import com.airline.dto.request.FlightScheduleRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightScheduleResponse;
import com.airline.entity.Flight;
import com.airline.entity.FlightSchedule;


public class FlightScheduleMapper {

    private FlightScheduleMapper() {
    }

    public static FlightSchedule toEntity(FlightScheduleRequest request, Flight flight) {
        if (request == null) {
            return null;
        }

        return FlightSchedule.builder()
                .flight(flight)
                .departureAirportId(flight.getDepartureAirportId())
                .arrivalAirportId(flight.getArrivalAirportId())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .operatingDays(request.getOperatingDays())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    public static void updateEntityFromRequest(FlightSchedule existing, FlightScheduleRequest request) {
        if (request == null || existing == null) return;
        if (request.getDepartureTime() != null) existing.setDepartureTime(request.getDepartureTime());
        if (request.getArrivalTime() != null) existing.setArrivalTime(request.getArrivalTime());
        if (request.getStartDate() != null) existing.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) existing.setEndDate(request.getEndDate());
        if (request.getOperatingDays() != null) existing.setOperatingDays(request.getOperatingDays());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());
    }

    public static FlightScheduleResponse toResponse(FlightSchedule flightSchedule, AirportResponse departureAirport, AirportResponse arrivalAirport) {
        if (flightSchedule == null) {
            return null;
        }

        return FlightScheduleResponse.builder()
                .id(flightSchedule.getId())
                .flightId(flightSchedule.getFlight().getId())
                .flightNumber(flightSchedule.getFlight().getFlightNumber())
                .departureAirport(departureAirport)
                .arrivalAirport(arrivalAirport)
                .departureTime(flightSchedule.getDepartureTime())
                .arrivalTime(flightSchedule.getArrivalTime())
                .startDate(flightSchedule.getStartDate())
                .endDate(flightSchedule.getEndDate())
                .operatingDays(flightSchedule.getOperatingDays())
                .isActive(flightSchedule.getIsActive())
                .build();
    }


}
