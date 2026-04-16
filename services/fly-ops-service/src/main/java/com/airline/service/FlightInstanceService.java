package com.airline.service;

import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.response.FlightInstanceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FlightInstanceService {

    FlightInstanceResponse createFlightInstance(FlightInstanceRequest flightInstanceRequest);

    FlightInstanceResponse getFlightInstanceById(Long id);

    FlightInstanceResponse updateFlightInstance(Long id, FlightInstanceRequest flightInstanceRequest);

    void deleteFlightInstance(Long id);

    Page<FlightInstanceResponse> getByAirlineId(Long airlineId, Long departureAirportId, Long arrivalAirportId,
                                                Long flightId,
                                                String departureTime, Long onDate, Pageable pageable);
}
