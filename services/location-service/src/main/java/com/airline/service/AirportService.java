package com.airline.service;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AirportService {
    AirportResponse createAirport(AirportRequest airportRequest) throws Exception;

    AirportResponse getAirportById(Long id) throws Exception;

    AirportResponse updateAirport(Long id, AirportRequest airportRequest) throws Exception;

    void deleteAirport(Long id) throws Exception;

    Page<AirportResponse> getAllAirports(Pageable pageable);

    Page<AirportResponse> searchAirports(String keyword, Pageable pageable);

    Page<AirportResponse> getAirportsByCity(Long cityId, Pageable pageable) throws Exception;

    boolean existsByIataCode(String iataCode);
}
