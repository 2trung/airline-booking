package com.airline.dto.response;

import com.airline.enums.FlightStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightResponse {

    Long id;
    String flightNumber;
    AirlineResponse airline;
    AircraftResponse aircraft;
    AirportResponse departureAirport;
    AirportResponse arrivalAirport;
    Instant departureTime;
    Instant arrivalTime;
    FlightStatus status;
    Double lowestPrice;
    Integer totalAvailableSeats;

    Instant createdAt;
    Instant updatedAt;
}
