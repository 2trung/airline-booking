package com.airline.service.impl;

import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.response.FlightInstanceResponse;
import com.airline.entity.Flight;
import com.airline.entity.FlightInstance;
import com.airline.exception.BadRequestException;
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

    @Override
    public FlightInstanceResponse createFlightInstance(FlightInstanceRequest flightInstanceRequest) {
        validateRequestBody(flightInstanceRequest);

        Flight flight = findFlightByIdOrThrow(flightInstanceRequest.getFlightId());
        FlightInstance entity = FlightInstanceMapper.toEntity(flightInstanceRequest, flight);
        validateInstance(entity);

        FlightInstance saved = flightInstanceRepository.save(entity);

        return FlightInstanceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightInstanceResponse getFlightInstanceById(Long id) {
        return FlightInstanceMapper.toResponse(findByIdOrThrow(id));
    }

    @Override
    public FlightInstanceResponse updateFlightInstance(Long id, FlightInstanceRequest flightInstanceRequest) {
        validateRequestBody(flightInstanceRequest);

        FlightInstance existing = findByIdOrThrow(id);
        Flight flight = findFlightByIdOrThrow(flightInstanceRequest.getFlightId());

        FlightInstanceMapper.updateEntityFromRequest(existing, flightInstanceRequest, flight);
        validateInstance(existing);

        FlightInstance updated = flightInstanceRepository.save(existing);
        return FlightInstanceMapper.toResponse(updated);
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
        ).map(FlightInstanceMapper::toResponse);
    }

    private FlightInstance findByIdOrThrow(Long id) {
        return flightInstanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight instance not found with id: " + id));
    }

    private Flight findFlightByIdOrThrow(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
    }

    private void validateRequestBody(FlightInstanceRequest request) {
        if (request == null) {
            throw new BadRequestException("Flight instance request body is required");
        }
        if (request.getFlightId() == null) {
            throw new BadRequestException("Flight ID is required");
        }
        if (request.getScheduleId() == null) {
            throw new BadRequestException("Schedule ID is required");
        }
    }

    private void validateInstance(FlightInstance instance) {
        if (instance.getDepartureAirportId() != null
                && instance.getDepartureAirportId().equals(instance.getArrivalAirportId())) {
            throw new BadRequestException("Departure and arrival airports must be different");
        }

        if (instance.getDepartureTime() == null || instance.getArrivalTime() == null) {
            throw new BadRequestException("Departure and arrival times are required");
        }

        if (!instance.getArrivalTime().isAfter(instance.getDepartureTime())) {
            throw new BadRequestException("Arrival time must be after departure time");
        }

        if (instance.getTotalSeats() == null || instance.getTotalSeats() <= 0) {
            throw new BadRequestException("Total seats must be a positive number");
        }

        if (instance.getAvailableSeats() == null || instance.getAvailableSeats() < 0) {
            throw new BadRequestException("Available seats must be zero or a positive number");
        }

        if (instance.getAvailableSeats() > instance.getTotalSeats()) {
            throw new BadRequestException("Available seats cannot exceed total seats");
        }

        if (instance.getMinAdvanceBookingDays() != null && instance.getMinAdvanceBookingDays() < 0) {
            throw new BadRequestException("Minimum advance booking days cannot be negative");
        }

        if (instance.getMaxAdvanceBookingDays() != null && instance.getMaxAdvanceBookingDays() < 0) {
            throw new BadRequestException("Maximum advance booking days cannot be negative");
        }

        if (instance.getMinAdvanceBookingDays() != null
                && instance.getMaxAdvanceBookingDays() != null
                && instance.getMinAdvanceBookingDays() > instance.getMaxAdvanceBookingDays()) {
            throw new BadRequestException("Minimum advance booking days cannot exceed maximum advance booking days");
        }
    }
}

