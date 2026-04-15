package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AircraftRequest {

    @NotBlank(message = "Aircraft code is required")
    private String code;

    @NotBlank(message = "Aircraft model is required")
    private String model;

    @NotBlank(message = "Aircraft manufacturer is required")
    private String manufacturer;

    @NotNull(message = "Seating capacity is required")
    @Positive(message = "Seating capacity must be a positive number")
    private Integer seatingCapacity;

    @Positive(message = "Economy seats must be a positive number")
    private Integer economySeats;

    @Positive(message = "Premium economy seats must be a positive number")
    private Integer premiumEconomySeats;

    @Positive(message = "Business seats must be a positive number")
    private Integer businessSeats;

    @Positive(message = "First class seats must be a positive number")
    private Integer firstClassSeats;

    @Positive(message = "Cruise speed must be a positive number")
    private Integer cruiseSpeed;

    @Positive(message = "Year of manufacture must be a positive number")
    private Integer yearOfManufacture;

    @Positive(message = "Range in km must be a positive number")
    private Integer rangeInKm;

    @Positive(message = "Max altitude in feet must be a positive number")
    private Integer maxAltitudeInFeet;

    @NotNull(message = "Owner id is required")
    private Long ownerId;

    private Long currentAirportId;

}
