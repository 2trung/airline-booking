package com.airline.dto.request;

import com.airline.enums.AircraftStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AircraftRequest {
    @NotBlank(message = "Aircraft code is required")
    String code;

    @NotBlank(message = "Aircraft model is required")
    String model;

    @NotBlank(message = "Manufacturer is required")
    String manufacturer;

    @NotNull(message = "Seating capacity is required")
    @Positive(message = "Seating capacity must be positive")
    Integer seatingCapacity;

    @Positive(message = "Economy seats must be positive")
    Integer economySeats;

    @Positive(message = "Premium economy seats must be positive")
    Integer premiumEconomySeats;

    @Positive(message = "Business seats must be positive")
    Integer businessSeats;

    @Positive(message = "First class seats must be positive")
    Integer firstClassSeats;

    @Positive(message = "Range must be positive")
    Integer rangeKm;

    @Positive(message = "Cruising speed must be positive")
    Integer cruisingSpeedKmh;

    @Positive(message = "Maximum altitude must be positive")
    Integer maxAltitudeFt;

    @Positive(message = "Year of manufacture must be positive")
    Integer yearOfManufacture;

    LocalDate registrationDate;
    LocalDate nextMaintenanceDate;

    @NotNull(message = "Status is required")
    AircraftStatus status;

    @NotNull(message = "Availability status is required")
    Boolean isAvailable;

    Long currentAirportId;

}
