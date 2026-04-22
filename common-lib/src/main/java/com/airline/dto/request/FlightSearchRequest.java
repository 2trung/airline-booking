package com.airline.dto.request;

import com.airline.enums.CabinClassType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightSearchRequest {

    Long departureAirportId;
    Long arrivalAirportId;

    @NotNull(message = "Departure date is required")
    LocalDate departureDate;

    @NotNull(message = "Number of passengers is required")
    @Min(value = 1, message = "At least 1 passenger is required")
    Integer passengers;

    @NotNull(message = "Cabin class is required")
    CabinClassType cabinClass;
    
    // User's timezone for date filtering (defaults to UTC if not provided)
    String timezone; // e.g., "America/New_York", "Europe/London", "Asia/Tokyo"

    // Filter Parameters
    List<Long> airlines; // Filter by airline ids
    Double minPrice; // Minimum price filter
    Double maxPrice; // Maximum price filter
    String departureTimeRange; // "any", "morning", "afternoon", "evening", "night"
    String arrivalTimeRange; // "any", "morning", "afternoon", "evening", "night"
    Integer maxDuration; // Maximum duration in minutes
    String alliance; // "any", "star", "oneworld", "skyteam"

    // Sorting Parameters
    String sortBy; // "price", "duration", "departure", "arrival"
    String sortOrder; // "asc", "desc"
}
