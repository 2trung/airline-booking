package com.airline.service.impl;

import com.airline.client.AirlineClient;
import com.airline.client.LocationClient;
import com.airline.dto.request.FlightRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightResponse;
import com.airline.entity.Flight;
import com.airline.exception.BadRequestException;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.FlightMapper;
import com.airline.repository.FlightRepository;
import com.airline.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineClient airlineClient;
    private final LocationClient locationClient;

    @Override
    public FlightResponse createFlight(FlightRequest flightRequest) {

        if (flightRepository.existsByFlightNumberIgnoreCase(flightRequest.getFlightNumber())) {
            throw new BadRequestException("Flight number " + flightRequest.getFlightNumber() + " already exists");
        }
        Flight flight = FlightMapper.toEntity(flightRequest);
        Flight saved = flightRepository.save(flight);
        return convertToFlightResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightResponse getFlightById(Long id) {
        return convertToFlightResponse(findByIdOrThrow(id));
    }

    @Override
    public FlightResponse updateFlight(Long id, FlightRequest flightRequest) {
        Flight existing = findByIdOrThrow(id);
        validateUpdateRequest(id, flightRequest);

        FlightMapper.updateEntityFromRequest(existing, flightRequest);
        validateFlightSchedule(existing.getDepartureDateTime(), existing.getArrivalDateTime());
        Flight updated = flightRepository.save(existing);
        return convertToFlightResponse(updated);
    }

    @Override
    public void deleteFlight(Long id) {
        Flight existing = findByIdOrThrow(id);
        flightRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightResponse> getFlightsByAirline(
            Long airlineId,
            Long departureAirportId,
            Long arrivalAirportId,
            Pageable pageable
    ) {
        return flightRepository.searchFlights(
                airlineId,
                departureAirportId,
                arrivalAirportId,
                pageable
        ).map(this::convertToFlightResponse);
    }

    private Flight findByIdOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
    }

    private void validateCreateRequest(FlightRequest flightRequest) {
        validateRequestBody(flightRequest);

        if (flightRepository.existsByFlightNumberIgnoreCase(flightRequest.getFlightNumber())) {
            throw new BadRequestException("Flight number " + flightRequest.getFlightNumber() + " already exists");
        }
    }

    private void validateUpdateRequest(Long id, FlightRequest flightRequest) {
        validateRequestBody(flightRequest);

        if (flightRepository.existsByFlightNumberIgnoreCaseAndIdNot(flightRequest.getFlightNumber(), id)) {
            throw new BadRequestException("Flight number " + flightRequest.getFlightNumber() + " already exists");
        }
    }

    private void validateRequestBody(FlightRequest flightRequest) {
        if (flightRequest == null) {
            throw new BadRequestException("Flight request body is required");
        }

        if (flightRequest.getAirlineId() == null) {
            throw new BadRequestException("Airline ID is required");
        }

        if (flightRequest.getDepartureAirportId() != null
                && flightRequest.getDepartureAirportId().equals(flightRequest.getArrivalAirportId())) {
            throw new BadRequestException("Departure and arrival airports must be different");
        }
    }

    private void validateFlightSchedule(Instant departureTime, Instant arrivalTime) {
        if (!arrivalTime.isAfter(departureTime)) {
            throw new BadRequestException("Arrival time must be after departure time");
        }
    }

    public FlightResponse convertToFlightResponse(Flight flight) {
        AircraftResponse aircraft = airlineClient.getAircraftById(flight.getAircraftId());
        AirlineResponse airline = airlineClient.getAirlineById(flight.getAirlineId());
        AirportResponse departureAirport = locationClient.getAirportById(flight.getDepartureAirportId());
        AirportResponse arrivalAirport = locationClient.getAirportById(flight.getArrivalAirportId());
        return FlightMapper.toResponse(flight, aircraft, airline, departureAirport, arrivalAirport);
    }
}

