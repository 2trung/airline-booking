package com.airline.service;

import com.airline.dto.request.FlightScheduleRequest;
import com.airline.dto.response.FlightScheduleResponse;
import com.airline.exception.AirportException;

import java.util.List;

public interface FlightScheduleService {
    FlightScheduleResponse createFlightSchedule(Long userId, FlightScheduleRequest request) throws Exception;
    FlightScheduleResponse getFlightScheduleById(Long id) throws AirportException;

    List<FlightScheduleResponse> getFlightScheduleByAirline(Long userId);

    FlightScheduleResponse updateFlightSchedule(Long id, FlightScheduleRequest request) throws AirportException;

    void deleteFlightSchedule(Long id);

}
