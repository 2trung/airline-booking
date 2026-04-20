package com.airline.dto.response;


import com.airline.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightInstanceResponse {
    Long id;

    Long flightId;
    String flightNumber;

    Long airlineId;
    String airlineName;
    String airlineLogo;
    Long aircraftId;
    String aircraftModal;
    String aircraftCode;
    AirportResponse departureAirport;
    AirportResponse arrivalAirport;

    LocalDateTime departureDateTime;
    LocalDateTime arrivalDateTime;
    String formattedDuration;

    Integer totalSeats;
    Integer availableSeats;

    FlightStatus status;

    Integer minAdvanceBookingDays;
    Integer maxAdvanceBookingDays;
    Boolean isActive;

    String terminal;
    String gate;

    Long version;
    FareResponse fare;
}
