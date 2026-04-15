package com.airline.mapper;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.CityResponse;
import com.airline.entity.Airport;
import com.airline.entity.City;

public class AirportMapper {
    public static Airport toEntity(AirportRequest airportRequest, City city) {
        if (airportRequest == null) return null;
        return Airport.builder()
                .name(airportRequest.getName())
                .iataCode(airportRequest.getIataCode())
                .address(airportRequest.getAddress())
                .geoCode(airportRequest.getGeoCode())
                .city(city)
                .build();
    }

    public static AirportResponse toResponse(Airport airport) {
        if (airport == null) return null;

        CityResponse cityResponse = null;
        if (airport.getCity() != null) {
            cityResponse = CityMapper.toResponse(airport.getCity());
        }

        return AirportResponse.builder()
                .id(airport.getId())
                .name(airport.getName())
                .iataCode(airport.getIataCode())
                .detailedName(airport.getDetailedName())
                .address(airport.getAddress())
                .geoCode(airport.getGeoCode())
                .city(cityResponse)
                .build();
    }

    public static void updateEntityFromDto(Airport airport, AirportRequest airportRequest, City city) {
        if (airportRequest.getName() != null)
            airport.setName(airportRequest.getName());
        if (airportRequest.getIataCode() != null)
            airport.setIataCode(airportRequest.getIataCode());
        if (airportRequest.getAddress() != null)
            airport.setAddress(airportRequest.getAddress());
        if (airportRequest.getGeoCode() != null)
            airport.setGeoCode(airportRequest.getGeoCode());
        if (city != null)
            airport.setCity(city);
    }
}

