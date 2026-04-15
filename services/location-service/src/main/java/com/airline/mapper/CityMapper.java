package com.airline.mapper;

import com.airline.dto.request.CityRequest;
import com.airline.dto.response.CityResponse;
import com.airline.entity.City;

public class CityMapper {
    public static City toEntity(CityRequest cityRequest) {
        if (cityRequest == null) return null;
        return City.builder()
                .name(cityRequest.getName())
                .cityCode(cityRequest.getCityCode())
                .countryCode(cityRequest.getCountryCode())
                .countryName(cityRequest.getCountryName())
                .regionCode(cityRequest.getRegionCode())
                .timeZone(cityRequest.getTimeZone())
                .build();
    }

    public static CityResponse toResponse(City city) {
        if (city == null) return null;
        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .cityCode(city.getCityCode())
                .countryCode(city.getCountryCode())
                .countryName(city.getCountryName())
                .regionCode(city.getRegionCode())
                .timeZone(city.getTimeZone())
                .build();
    }

    public static void updateEntityFromDto(City city, CityRequest cityRequest) {
        if (cityRequest.getName() != null)
            city.setName(cityRequest.getName());
        if (cityRequest.getCityCode() != null)
            city.setCityCode(cityRequest.getCityCode());
        if (cityRequest.getCountryCode() != null)
            city.setCountryCode(cityRequest.getCountryCode());
        if (cityRequest.getCountryName() != null)
            city.setCountryName(cityRequest.getCountryName());
        if (cityRequest.getRegionCode() != null)
            city.setRegionCode(cityRequest.getRegionCode());
        if (cityRequest.getTimeZone() != null)
            city.setTimeZone(cityRequest.getTimeZone());
    }
}
