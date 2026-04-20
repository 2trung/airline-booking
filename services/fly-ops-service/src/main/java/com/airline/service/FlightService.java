package com.airline.service;

import com.airline.dto.request.FlightRequest;
import com.airline.dto.response.FlightResponse;
import com.airline.enums.FlightStatus;
import com.airline.exception.AirportException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FlightService {

    FlightResponse createFlight(Long userId, FlightRequest request) throws AirportException;
    List<FlightResponse> createFlights(Long userId, List<FlightRequest> requests) throws AirportException;
    FlightResponse getFlightById(Long id);
    FlightResponse getFlightByNumber(String flightNumber) throws AirportException;
    Page<FlightResponse> getFlightsByAirline(Long userId,
                                             Long departureAirportId,
                                             Long arrivalAirportId,
                                             Pageable pageable);
    FlightResponse updateFlight(Long id, FlightRequest request) throws AirportException;
    FlightResponse changeStatus(Long id, FlightStatus status) throws AirportException;
    void deleteFlight(Long id);

    Map<Long, FlightResponse> getFlightsByIds(List<Long> ids);
}
