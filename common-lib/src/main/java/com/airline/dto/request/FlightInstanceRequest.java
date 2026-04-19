package com.airline.dto.request;

import com.airline.enums.FlightStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightInstanceRequest {
    @NotNull(message = "Flight ID is required")
    private Long flightId;

    private Long airlineId;

    private Long scheduleId;

    private Long departureAirportId;
    private Long arrivalAirportId;

    private String flightNumber;

    @NotNull(message = "Departure time is required")
    private Instant departureDateTime;

    @NotNull(message = "Arrival time is required")
    private Instant arrivalDateTime;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be a positive number")
    private Integer totalSeats;

    @PositiveOrZero(message = "Available seats must be zero or a positive number")
    private Integer availableSeats;

    private FlightStatus status;

    private Integer minAdvanceBookingDays;
    private Integer maxAdvanceBookingDays;
    private Boolean isActive;


}
