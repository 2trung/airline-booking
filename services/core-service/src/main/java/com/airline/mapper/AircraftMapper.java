package com.airline.mapper;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.entity.Aircraft;

public class AircraftMapper {

    private AircraftMapper() {
    }

    public static Aircraft toEntity(AircraftRequest request) {
        if (request == null) {
            return null;
        }

        return Aircraft.builder()
                .code(normalizeCode(request.getCode()))
                .model(request.getModel())
                .manufacturer(request.getManufacturer())
                .capacity(request.getSeatingCapacity())
                .economySeats(defaultZero(request.getEconomySeats()))
                .premiumEconomySeats(defaultZero(request.getPremiumEconomySeats()))
                .businessSeats(defaultZero(request.getBusinessSeats()))
                .firstClassSeats(defaultZero(request.getFirstClassSeats()))
                .cruiseSpeed(request.getCruiseSpeed())
                .yearOfManufacture(request.getYearOfManufacture())
                .rangeInKm(request.getRangeInKm())
                .maxAltitudeInFeet(request.getMaxAltitudeInFeet())
                .currentAirportId(request.getCurrentAirportId())
                .build();
    }

    public static void updateEntityFromRequest(Aircraft aircraft, AircraftRequest request) {
        if (aircraft == null || request == null) {
            return;
        }

        aircraft.setCode(normalizeCode(request.getCode()));
        aircraft.setModel(request.getModel());
        aircraft.setManufacturer(request.getManufacturer());
        aircraft.setCapacity(request.getSeatingCapacity());
        aircraft.setEconomySeats(defaultZero(request.getEconomySeats()));
        aircraft.setPremiumEconomySeats(defaultZero(request.getPremiumEconomySeats()));
        aircraft.setBusinessSeats(defaultZero(request.getBusinessSeats()));
        aircraft.setFirstClassSeats(defaultZero(request.getFirstClassSeats()));
        aircraft.setCruiseSpeed(request.getCruiseSpeed());
        aircraft.setYearOfManufacture(request.getYearOfManufacture());
        aircraft.setRangeInKm(request.getRangeInKm());
        aircraft.setMaxAltitudeInFeet(request.getMaxAltitudeInFeet());
        aircraft.setCurrentAirportId(request.getCurrentAirportId());
    }

    public static AircraftResponse toResponse(Aircraft aircraft) {
        if (aircraft == null) {
            return null;
        }

        return AircraftResponse.builder()
                .id(aircraft.getId())
                .model(aircraft.getModel())
                .code(aircraft.getCode())
                .manufacturer(aircraft.getManufacturer())
                .seatingCapacity(aircraft.getCapacity())
                .economySeats(aircraft.getEconomySeats())
                .premiumEconomySeats(aircraft.getPremiumEconomySeats())
                .businessSeats(aircraft.getBusinessSeats())
                .firstClassSeats(aircraft.getFirstClassSeats())
                .rangeInKm(aircraft.getRangeInKm())
                .cruiseSpeed(aircraft.getCruiseSpeed())
                .yearOfManufacture(aircraft.getYearOfManufacture())
                .registrationDate(aircraft.getRegistrationDate())
                .nextMaintenanceDate(aircraft.getNextMaintenanceDate())
                .status(aircraft.getStatus())
                .airlineId(aircraft.getAirline() == null ? null : aircraft.getAirline().getId())
                .airlineName(aircraft.getAirline() == null ? null : aircraft.getAirline().getName())
                .airlineIataCode(aircraft.getAirline() == null ? null : aircraft.getAirline().getIataCode())
                .currentAirportId(aircraft.getCurrentAirportId())
                .totalSeats(aircraft.getTotalSeats())
                .requiresMaintenance(aircraft.requiresMaintenance())
                .isOperational(aircraft.isOperational())
                .createdAt(aircraft.getCreatedAt())
                .updatedAt(aircraft.getUpdatedAt())
                .build();
    }

    private static int defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }
}

