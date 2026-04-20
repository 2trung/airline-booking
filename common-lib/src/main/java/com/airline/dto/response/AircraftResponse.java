package com.airline.dto.response;

import com.airline.enums.AircraftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AircraftResponse {
    Long id;
    String code;
    String model;
    String manufacturer;
    Integer seatingCapacity;
    Integer economySeats;
    Integer premiumEconomySeats;
    Integer businessSeats;
    Integer firstClassSeats;
    Integer rangeKm;
    Integer cruisingSpeedKmh;
    Integer maxAltitudeFt;
    Integer yearOfManufacture;
    LocalDate registrationDate;
    LocalDate nextMaintenanceDate;
    AircraftStatus status;
    Boolean isAvailable;

    Long airlineId;
    String airlineName;
    String airlineIataCode;

    Long currentAirportId;
    Long currentAirportCity;
    String currentAirportCode;
    String currentAirportName;

    Integer totalSeats;
    Boolean requiresMaintenance;
    Boolean isOperational;

    Instant createdAt;
    Instant updatedAt;
}
