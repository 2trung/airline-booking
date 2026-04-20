package com.airline.service.impl;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import com.airline.entity.Airline;
import com.airline.enums.AirlineStatus;
import com.airline.exception.BadRequestException;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.AirlineMapper;
import com.airline.repository.AirlineRepository;
import com.airline.service.AirlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    public AirlineResponse createAirline(AirlineRequest airlineRequest) {
        validateCreateRequest(airlineRequest);
        Airline entity = AirlineMapper.toEntity(airlineRequest);
        Airline saved = airlineRepository.save(entity);
        return AirlineMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponse getAirlineByOwnerId(Long ownerId) {
        Airline airline = airlineRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found for owner id: " + ownerId));
        return AirlineMapper.toResponse(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public AirlineResponse getAirlineById(Long id) {
        Airline airline = findByIdOrThrow(id);
        return AirlineMapper.toResponse(airline);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirlineResponse> getAllAirlines(Pageable pageable) {
        return airlineRepository.findAll(pageable).map(AirlineMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirlineResponse> getAllAirlinesWithKeyword(String keyword, Pageable pageable) {
        return airlineRepository.searchByKeyword(keyword == null ? "" : keyword.trim(), pageable)
                .map(AirlineMapper::toResponse);
    }

    @Override
    public AirlineResponse updateAirline(Long id, AirlineRequest airlineRequest) {
        Airline existingAirline = findByIdOrThrow(id);
        validateUpdateRequest(id, airlineRequest);

        AirlineMapper.updateEntityFromRequest(existingAirline, airlineRequest);
        Airline updated = airlineRepository.save(existingAirline);
        return AirlineMapper.toResponse(updated);
    }

    @Override
    public void deleteAirline(Long id) {
        Airline airline = findByIdOrThrow(id);
        airlineRepository.delete(airline);
    }

    @Override
    public AirlineResponse changeAirlineStatus(Long id, String status) {
        Airline airline = findByIdOrThrow(id);
        try {
            AirlineStatus airlineStatus = AirlineStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            airline.setStatus(airlineStatus);
            return AirlineMapper.toResponse(airlineRepository.save(airline));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid airline status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineDropdownItem> getAirlinesForDropdown() {
        return airlineRepository.findAllByOrderByNameAsc()
                .stream()
                .map(AirlineMapper::toDropdownItem)
                .toList();
    }

    private Airline findByIdOrThrow(Long id) {
        return airlineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found with id: " + id));
    }

    private void validateCreateRequest(AirlineRequest airlineRequest) {
        if (airlineRequest == null) {
            throw new BadRequestException("Airline request body is required");
        }

        if (airlineRepository.existsByIataCodeIgnoreCase(airlineRequest.getIataCode())) {
            throw new BadRequestException("Airline with IATA code " + airlineRequest.getIataCode() + " already exists");
        }

        if (airlineRepository.existsByIcaoCodeIgnoreCase(airlineRequest.getIcaoCode())) {
            throw new BadRequestException("Airline with ICAO code " + airlineRequest.getIcaoCode() + " already exists");
        }
    }

    private void validateUpdateRequest(Long id, AirlineRequest airlineRequest) {
        if (airlineRequest == null) {
            throw new BadRequestException("Airline request body is required");
        }

        if (airlineRepository.existsByIataCodeIgnoreCaseAndIdNot(airlineRequest.getIataCode(), id)) {
            throw new BadRequestException("Airline with IATA code " + airlineRequest.getIataCode() + " already exists");
        }

        if (airlineRepository.existsByIcaoCodeIgnoreCaseAndIdNot(airlineRequest.getIcaoCode(), id)) {
            throw new BadRequestException("Airline with ICAO code " + airlineRequest.getIcaoCode() + " already exists");
        }
    }
}

