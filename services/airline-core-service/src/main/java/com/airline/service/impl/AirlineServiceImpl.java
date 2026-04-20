package com.airline.service.impl;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import com.airline.entity.Airline;
import com.airline.enums.AirlineStatus;
import com.airline.mapper.AirlineMapper;
import com.airline.repository.AirlineRepository;
import com.airline.service.AirlineService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    public AirlineResponse createAirline(AirlineRequest request, Long ownerId) {
        Airline airline = AirlineMapper.toEntity(request, ownerId);
        Airline saved = airlineRepository.save(airline);
        return AirlineMapper.toResponse(saved);
    }

    @Override
    public AirlineResponse getAirlineByOwner(Long ownerId) {
        Airline airline = airlineRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found for owner: " + ownerId));
        return AirlineMapper.toResponse(airline);
    }

    @Override
    public AirlineResponse getAirlineById(Long id) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found"));
        return AirlineMapper.toResponse(airline);
    }

    @Override
    public Page<AirlineResponse> getAllAirlines(Pageable pageable) {
        return airlineRepository
                .findAll(pageable).map(AirlineMapper::toResponse);
    }

    @Override
    public AirlineResponse updateAirline(AirlineRequest request, Long ownerId) {
        Airline airline = airlineRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found for owner: " + ownerId));

        AirlineMapper.updateEntity(airline, request);
        return AirlineMapper.toResponse(airlineRepository.save(airline));
    }

    @Override
    public void deleteAirline(Long id, Long ownerId) {
        Airline airline = airlineRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found for owner: " + ownerId));
        airlineRepository.delete(airline);
    }

    @Override
    public AirlineResponse changeStatusByAdmin(Long airlineId, AirlineStatus status) {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new EntityNotFoundException("Airline not found with ID: " + airlineId));
        airline.setStatus(status);
        return AirlineMapper.toResponse(airlineRepository.save(airline));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AirlineDropdownItem> getAirlinesForDropdown() {
        return airlineRepository.findByStatus(AirlineStatus.ACTIVE).stream()
                .map(a -> AirlineDropdownItem.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .iataCode(a.getIataCode())
                        .icaoCode(a.getIcaoCode())
                        .logoUrl(a.getLogoUrl())
                        .country(a.getCountry())
                        .build())
                .toList();
    }
}

