package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightScheduleResponse {
    private Long id;
    private Long flightId;
    private String flightNumber;

    private AirportResponse departureAirport;
    private AirportResponse arrivalAirport;

    private Instant departureTime;
    private Instant arrivalTime;

    private LocalDate startDate;
    private LocalDate endDate;

    private Set<DayOfWeek> operatingDays;

    private Boolean isActive;
}
