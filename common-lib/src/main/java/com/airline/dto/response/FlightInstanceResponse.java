package com.airline.dto.response;

import com.airline.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInstanceResponse {
    private Long id;
    private Long flightId;
    private String flightNumber;
    private Long airlineId;
    private String airlineName;
    private String airlineLogo;
    private Long aircraftId;
    private String aircraftModel;
    private String aircraftCode;
    private AirportResponse departureAirport;
    private AirportResponse arrivalAirport;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String formatDuration;

    private Integer totalSeats;
    private Integer availableSeats;

    private FlightStatus status;

    private Integer minAdvanceBookingDays;
    private Integer maxAdvanceBookingDays;

    private Boolean isActive;

    private String terminal;

    private String gate;

    private Long version;
}
