package com.airline.dto.request;

import com.airline.enums.FlightStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class FlightInstanceRequest {

    @NotNull(message = "Flight ID is required")
    Long flightId;

    Long airlineId;

    Long scheduleId;

    Long departureAirportId;

    Long arrivalAirportId;

    @NotNull(message = "Departure date-time is required")
    LocalDateTime departureDateTime;

    @NotNull(message = "Arrival date-time is required")
    LocalDateTime arrivalDateTime;

    @NotNull(message = "Total seats is required")
    @Positive
    Integer totalSeats;

    @PositiveOrZero
    Integer availableSeats;

    FlightStatus status;

    Integer minAdvanceBookingDays;
    Integer maxAdvanceBookingDays;
    Boolean isActive;

    String terminal;
    String gate;
}
