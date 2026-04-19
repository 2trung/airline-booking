package com.airline.service.impl;

import com.airline.client.AirlineClient;
import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.AirportResponse;
import com.airline.dto.response.FlightInstanceResponse;
import com.airline.entity.Flight;
import com.airline.entity.FlightInstance;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.FlightInstanceMapper;
import com.airline.repository.FlightInstanceRepository;
import com.airline.repository.FlightRepository;
import com.airline.service.FlightInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightInstanceServiceImpl implements FlightInstanceService {

    private final FlightInstanceRepository flightInstanceRepository;
    private final FlightRepository flightRepository;
    private final AirlineClient airlineClient;

    @Override
    public FlightInstanceResponse createFlightInstance(FlightInstanceRequest flightInstanceRequest) {

        Flight flight = findFlightByIdOrThrow(flightInstanceRequest.getFlightId());


        AircraftResponse aircraftResponse = airlineClient.getAircraftById(flight.getAircraftId());
        FlightInstance entity = FlightInstanceMapper.toEntity(flightInstanceRequest, flight);
        entity.setTotalSeats(aircraftResponse.getTotalSeats());
        entity.setAvailableSeats(aircraftResponse.getTotalSeats());

        FlightInstance saved = flightInstanceRepository.save(entity);
        // todo: publish kafka event to create seat instance
        return convertToFlightInstanceResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightInstanceResponse getFlightInstanceById(Long id) {
        return convertToFlightInstanceResponse(findByIdOrThrow(id));
    }

    @Override
    public FlightInstanceResponse updateFlightInstance(Long id, FlightInstanceRequest flightInstanceRequest) {

        FlightInstance existing = findByIdOrThrow(id);
        Flight flight = findFlightByIdOrThrow(flightInstanceRequest.getFlightId());

        FlightInstanceMapper.updateEntityFromRequest(existing, flightInstanceRequest, flight);

        FlightInstance updated = flightInstanceRepository.save(existing);
        return convertToFlightInstanceResponse(updated);
    }

    @Override
    public void deleteFlightInstance(Long id) {
        FlightInstance existing = findByIdOrThrow(id);
        flightInstanceRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightInstanceResponse> getByAirlineId(
            Long airlineId,
            Long departureAirportId,
            Long arrivalAirportId,
            Long flightId,
            String departureTime,
            Long onDate,
            Pageable pageable
    ) {
        LocalDateTime parsedDepartureTime = FlightInstanceMapper.parseOptionalDateTime(departureTime, "departureTime");
        LocalDateTime[] onDateRange = FlightInstanceMapper.parseOnDateRange(onDate, "onDate");

        return flightInstanceRepository.searchFlightInstances(
                airlineId,
                departureAirportId,
                arrivalAirportId,
                flightId,
                parsedDepartureTime,
                onDateRange[0],
                onDateRange[1],
                pageable
        ).map(this::convertToFlightInstanceResponse);
    }

    private FlightInstance findByIdOrThrow(Long id) {
        return flightInstanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight instance not found with id: " + id));
    }

    private Flight findFlightByIdOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
    }


    private FlightInstanceResponse convertToFlightInstanceResponse(FlightInstance flightInstance) {
        AirlineResponse airlineResponse = airlineClient.getAirlineById(flightInstance.getFlight().getAirlineId());
        AircraftResponse aircraftResponse = airlineClient.getAircraftById(flightInstance.getFlight().getAircraftId());
        AirportResponse departureAirportResponse = airlineClient.getAirportById(flightInstance.getFlight().getDepartureAirportId());
        AirportResponse arrivalAirportResponse = airlineClient.getAirportById(flightInstance.getFlight().getArrivalAirportId());

        return FlightInstanceMapper.toResponse(flightInstance, aircraftResponse, airlineResponse, departureAirportResponse, arrivalAirportResponse);
    }

}

