package com.airline.mapper;

import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightInstanceResponse;
import com.airline.entity.Flight;
import com.airline.entity.FlightInstance;
import com.airline.enums.FlightStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FlightInstanceMapper {

    private FlightInstanceMapper() {
    }

    public static FlightInstance toEntity(FlightInstanceRequest request, Flight flight) {
        if (request == null || flight == null) {
            return null;
        }

        Integer totalSeats = request.getTotalSeats();
        Integer availableSeats = request.getAvailableSeats() == null ? totalSeats : request.getAvailableSeats();

        return FlightInstance.builder()
                .airlineId(request.getAirlineId() == null ? flight.getAirlineId() : request.getAirlineId())
                .flight(flight)
                .departureAirportId(request.getDepartureAirportId() == null ? flight.getDepartureAirportId() : request.getDepartureAirportId())
                .arrivalAirportId(request.getArrivalAirportId() == null ? flight.getArrivalAirportId() : request.getArrivalAirportId())
                .scheduleId(request.getScheduleId())
                .departureTime(request.getDepartureDateTime())
                .arrivalTime(request.getArrivalDateTime())
                .totalSeats(totalSeats)
                .availableSeats(availableSeats)
                .status(request.getStatus() == null ? FlightStatus.SCHEDULED : request.getStatus())
                .minAdvanceBookingDays(request.getMinAdvanceBookingDays())
                .maxAdvanceBookingDays(request.getMaxAdvanceBookingDays())
                .isActive(request.getIsActive() == null ? Boolean.TRUE : request.getIsActive())
                .build();
    }

    public static void updateEntityFromRequest(FlightInstance entity, FlightInstanceRequest request, Flight flight) {
        if (entity == null || request == null || flight == null) {
            return;
        }

        entity.setFlight(flight);
        entity.setAirlineId(request.getAirlineId() == null ? flight.getAirlineId() : request.getAirlineId());
        entity.setDepartureAirportId(request.getDepartureAirportId() == null ? flight.getDepartureAirportId() : request.getDepartureAirportId());
        entity.setArrivalAirportId(request.getArrivalAirportId() == null ? flight.getArrivalAirportId() : request.getArrivalAirportId());
        entity.setScheduleId(request.getScheduleId());
        entity.setDepartureTime(request.getDepartureDateTime());
        entity.setArrivalTime(request.getArrivalDateTime());
        entity.setTotalSeats(request.getTotalSeats());
        entity.setAvailableSeats(request.getAvailableSeats() == null ? request.getTotalSeats() : request.getAvailableSeats());
        entity.setStatus(request.getStatus() == null ? entity.getStatus() : request.getStatus());
        entity.setMinAdvanceBookingDays(request.getMinAdvanceBookingDays());
        entity.setMaxAdvanceBookingDays(request.getMaxAdvanceBookingDays());
        entity.setIsActive(request.getIsActive() == null ? entity.getIsActive() : request.getIsActive());
    }

    public static FlightInstanceResponse toResponse(FlightInstance entity) {
        if (entity == null) {
            return null;
        }

        Flight flight = entity.getFlight();

        return FlightInstanceResponse.builder()
                .id(entity.getId())
                .flightId(flight == null ? null : flight.getId())
                .flightNumber(flight == null ? null : flight.getFlightNumber())
                .airlineId(entity.getAirlineId())
                .aircraftId(flight == null ? null : flight.getAircraftId())
                .departureAirport(AirportResponse.builder().id(entity.getDepartureAirportId()).build())
                .arrivalAirport(AirportResponse.builder().id(entity.getArrivalAirportId()).build())
                .departureTime(entity.getDepartureTime())
                .arrivalTime(entity.getArrivalTime())
                .formatDuration(entity.getFormattedDuration())
                .totalSeats(entity.getTotalSeats())
                .availableSeats(entity.getAvailableSeats())
                .status(entity.getStatus())
                .minAdvanceBookingDays(entity.getMinAdvanceBookingDays())
                .maxAdvanceBookingDays(entity.getMaxAdvanceBookingDays())
                .isActive(entity.getIsActive())
                .build();
    }

    public static LocalDateTime parseOptionalDateTime(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return LocalDateTime.parse(value.trim());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": " + value + ". Expected ISO-8601 date-time");
        }
    }

    public static LocalDateTime[] parseOnDateRange(Long onDate, String fieldName) {
        if (onDate == null) {
            return new LocalDateTime[]{null, null};
        }

        try {
            LocalDate localDate = Instant.ofEpochMilli(onDate)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();
            LocalDateTime from = localDate.atStartOfDay();
            return new LocalDateTime[]{from, from.plusDays(1)};
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": " + onDate + ". Expected epoch milliseconds");
        }
    }
}

