package com.airline.service.impl;

import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.request.FlightScheduleRequest;
import com.airline.dto.response.FlightScheduleResponse;
import com.airline.entity.Flight;
import com.airline.entity.FlightSchedule;
import com.airline.enums.FlightStatus;
import com.airline.exception.BadRequestException;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.FlightScheduleMapper;
import com.airline.repository.FlightRepository;
import com.airline.repository.FlightScheduleRepository;
import com.airline.service.FlightInstanceService;
import com.airline.service.FlightScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlightScheduleServiceImpl implements FlightScheduleService {

    private final FlightScheduleRepository flightScheduleRepository;
    private final FlightRepository flightRepository;
    private final FlightInstanceService flightInstanceService;

    @Override
    public FlightScheduleResponse createFlightSchedule(FlightScheduleRequest flightScheduleRequest) {
        validateCreateRequest(flightScheduleRequest);
        Flight flight = findFlightByIdOrThrow(flightScheduleRequest.getFlightId());

        if (flightScheduleRequest.getEndDate().isBefore(flightScheduleRequest.getStartDate())) {
            throw new BadRequestException("End date must be after or equal to start date");
        }
        
        FlightSchedule flightSchedule = FlightScheduleMapper.toEntity(flightScheduleRequest, flight);
        validateScheduleDates(flightSchedule);
        
        FlightSchedule saved = flightScheduleRepository.save(flightSchedule);
        Set<DayOfWeek> operatingDays = flightSchedule.getOperatingDays();
        LocalDate startDate = flightSchedule.getStartDate();
        LocalDate endDate = flightSchedule.getEndDate();

        FlightInstanceRequest flightInstanceRequest = FlightInstanceRequest
                .builder()
                .scheduleId(saved.getId())
                .flightId(flight.getId())
                .departureAirportId(flight.getDepartureAirportId())
                .arrivalAirportId(flight.getArrivalAirportId())
                .status(FlightStatus.SCHEDULED)
                .build();
        for (int day = startDate.getDayOfWeek().getValue(); day <= endDate.getDayOfWeek().getValue(); day++) {
            if (operatingDays.contains(DayOfWeek.of(day))) {
                flightInstanceRequest.setDepartureDateTime(flight.getDepartureDateTime());
                flightInstanceRequest.setArrivalDateTime(flight.getArrivalDateTime());
                flightInstanceService.createFlightInstance(flightInstanceRequest);
            }
        }

        return FlightScheduleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FlightScheduleResponse getFlightScheduleById(Long id) {
        FlightSchedule flightSchedule = findByIdOrThrow(id);
        return FlightScheduleMapper.toResponse(flightSchedule);
    }

    @Override
    public void deleteFlightSchedule(Long id) {
        FlightSchedule existing = findByIdOrThrow(id);
        flightScheduleRepository.delete(existing);
    }

    @Override
    public void updateFlightSchedule(Long id, FlightScheduleRequest flightScheduleRequest) {
        FlightSchedule existing = findByIdOrThrow(id);
        validateUpdateRequest(flightScheduleRequest);
        
        FlightScheduleMapper.updateEntityFromRequest(existing, flightScheduleRequest);
        validateScheduleDates(existing);
        
        flightScheduleRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightScheduleResponse> getFlightSchedulesByAirline(Long airlineId) {
        if (airlineId == null) {
            throw new BadRequestException("Airline ID is required");
        }
        
        List<FlightSchedule> schedules = flightScheduleRepository.findByAirlineId(airlineId);
        return schedules.stream()
                .map(FlightScheduleMapper::toResponse)
                .collect(Collectors.toList());
    }

    private FlightSchedule findByIdOrThrow(Long id) {
        return flightScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight schedule not found with id: " + id));
    }

    private Flight findFlightByIdOrThrow(Long flightId) {
        return flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + flightId));
    }

    private void validateCreateRequest(FlightScheduleRequest request) {
        validateRequestBody(request);
    }

    private void validateUpdateRequest(FlightScheduleRequest request) {
        validateRequestBody(request);
    }

    private void validateRequestBody(FlightScheduleRequest request) {
        if (request == null) {
            throw new BadRequestException("Flight schedule request body is required");
        }

        if (request.getFlightId() == null) {
            throw new BadRequestException("Flight ID is required");
        }

        if (request.getDepartureTime() == null) {
            throw new BadRequestException("Departure time is required");
        }

        if (request.getArrivalTime() == null) {
            throw new BadRequestException("Arrival time is required");
        }

        if (request.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }

        if (request.getEndDate() == null) {
            throw new BadRequestException("End date is required");
        }
    }

    private void validateScheduleDates(FlightSchedule flightSchedule) {
        // Note: Entity uses LocalTime instead of LocalDate for dates - type mismatch
        // This validation is a placeholder until entity is fixed
        if (flightSchedule.getStartDate() != null && flightSchedule.getEndDate() != null) {
            if (flightSchedule.getEndDate().isBefore(flightSchedule.getStartDate())) {
                throw new BadRequestException("End date must be after or equal to start date");
            }
        }

        if (flightSchedule.getArrivalTime() != null && flightSchedule.getDepartureTime() != null) {
            if (!flightSchedule.getArrivalTime().isAfter(flightSchedule.getDepartureTime())) {
                throw new BadRequestException("Arrival time must be after departure time");
            }
        }
    }
}
