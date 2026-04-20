package com.airline.service;

import com.airline.dto.request.CityRequest;
import com.airline.dto.response.CityResponse;
import com.airline.exception.OperationNotPermittedException;
import com.airline.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CityService {
    CityResponse createCity(CityRequest request) throws OperationNotPermittedException;
    List<CityResponse> createBulkCities(List<CityRequest> requests) throws OperationNotPermittedException;
    CityResponse getCityById(Long id) throws ResourceNotFoundException;

    CityResponse updateCity(Long id, CityRequest request) throws ResourceNotFoundException, OperationNotPermittedException;
    void deleteCity(Long id) throws ResourceNotFoundException;
    Page<CityResponse> getAllCities(Pageable pageable);

    Page<CityResponse> searchCities(String keyword, Pageable pageable);
    Page<CityResponse> getCitiesByCountryCode(String countryCode, Pageable pageable);

    boolean cityExists(String cityCode);
    boolean validateCityCode(String cityCode);
}
