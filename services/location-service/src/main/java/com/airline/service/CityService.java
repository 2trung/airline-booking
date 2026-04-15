package com.airline.service;

import com.airline.dto.request.CityRequest;
import com.airline.dto.response.CityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityService {
    CityResponse createCity(CityRequest cityRequest) throws Exception;

    CityResponse getCityById(Long id) throws Exception;

    CityResponse updateCity(Long id, CityRequest cityRequest) throws Exception;

    void deleteCity(Long id) throws Exception;

    Page<CityResponse> getAllCities(Pageable pageable);

    Page<CityResponse> searchCities(String keyword, Pageable pageable);

    Page<CityResponse> getCityByCountryCode(String countryCode, Pageable pageable);

    boolean existsByCityCode(String cityCode);
}
