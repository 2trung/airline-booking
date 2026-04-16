package com.airline.service;

import com.airline.dto.request.FlightScheduleRequest;
import com.airline.dto.response.FlightScheduleResponse;

import java.util.List;

public interface FlightScheduleService {
    FlightScheduleResponse createFlightSchedule(FlightScheduleRequest flightScheduleRequest);

    FlightScheduleResponse getFlightScheduleById(Long id);

    void deleteFlightSchedule(Long id);

    void updateFlightSchedule(Long id, FlightScheduleRequest flightScheduleRequest);

    List<FlightScheduleResponse> getFlightSchedulesByAirline(Long airlineId);

}
