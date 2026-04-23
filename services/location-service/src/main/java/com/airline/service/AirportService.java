package com.airline.service;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.exception.AirportException;
import com.airline.exception.CityException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirportService {
    AirportResponse createAirport(AirportRequest request) throws AirportException, CityException;

    List<AirportResponse> createBulkAirports(List<AirportRequest> requests) throws AirportException, CityException;

    AirportResponse getAirportById(Long id);

    List<AirportResponse> getAllAirports();

    AirportResponse updateAirport(Long id, AirportRequest request) throws AirportException, CityException;

    void deleteAirport(Long id) throws AirportException;

    List<AirportResponse> getAirportsByCityId(Long cityId);

    Page<AirportResponse> searchAirports(String keyword, Pageable pageable);
}
