package com.airline.service;

import com.airline.dto.request.FlightRequest;
import com.airline.dto.response.FlightResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightService {

    FlightResponse createFlight(FlightRequest flightRequest);

    FlightResponse getFlightById(Long id);

    FlightResponse updateFlight(Long id, FlightRequest flightRequest);

    void deleteFlight(Long id);

    Page<FlightResponse> getFlightsByAirline(Long airlineId, Long departureAirportId, Long arrivalAirportId, Pageable pageable);

}
