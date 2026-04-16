package com.airline.dto.request;

import com.airline.enums.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightRequest {
    @NotBlank(message = "Flight number is required")
    @Size(max = 10, message = "Flight number must be at most 10 characters")
    private String flightNumber;

    @NotNull(message = "Airline ID is required")
    private Long airlineId;

    @NotNull(message = "Aircraft ID is required")
    private Long aircraftId;

    @NotNull(message = "Departure airport ID is required")
    private Long departureAirportId;

    @NotNull(message = "Arrival airport ID is required")
    private Long arrivalAirportId;

    private FlightStatus status;
}
