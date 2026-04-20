package com.airline.dto.request;

import com.airline.enums.FlightStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightRequest {

    @NotBlank(message = "Flight number is required")
    @Size(max = 10)
    String flightNumber;

    Long airlineId;

    @NotNull(message = "Aircraft ID is required")
    Long aircraftId;

    @NotNull(message = "Departure airport ID is required")
    Long departureAirportId;

    @NotNull(message = "Arrival airport ID is required")
    Long arrivalAirportId;

    FlightStatus status;
}
