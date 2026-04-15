package com.airline.service.impl;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.entity.Aircraft;
import com.airline.entity.Airline;
import com.airline.exception.BadRequestException;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.AircraftMapper;
import com.airline.repository.AircraftRepository;
import com.airline.repository.AirlineRepository;
import com.airline.service.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AircraftServiceImpl implements AircraftService {

    private final AircraftRepository aircraftRepository;
    private final AirlineRepository airlineRepository;

    @Override
    public AircraftResponse createAircraft(AircraftRequest aircraftRequest) {
        validateCreate(aircraftRequest);
        Airline airline = getAirlineForOwner(aircraftRequest.getOwnerId());

        Aircraft aircraft = AircraftMapper.toEntity(aircraftRequest);
        aircraft.setAirline(airline);

        return AircraftMapper.toResponse(aircraftRepository.save(aircraft));
    }

    @Override
    public AircraftResponse updateAircraft(Long id, AircraftRequest aircraftRequest) {
        Aircraft existing = findAircraftById(id);
        validateUpdate(id, aircraftRequest);

        Airline airline = getAirlineForOwner(aircraftRequest.getOwnerId());
        AircraftMapper.updateEntityFromRequest(existing, aircraftRequest);
        existing.setAirline(airline);

        return AircraftMapper.toResponse(aircraftRepository.save(existing));
    }

    @Override
    public void deleteAircraft(Long id) {
        Aircraft aircraft = findAircraftById(id);
        aircraftRepository.delete(aircraft);
    }

    @Override
    @Transactional(readOnly = true)
    public AircraftResponse getAircraftById(Long id) {
        return AircraftMapper.toResponse(findAircraftById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftResponse> getAircraftsByOwnerId(Long ownerId) {
        return aircraftRepository.findByAirlineOwnerId(ownerId)
                .stream()
                .map(AircraftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AircraftResponse> getAircraftsByOwnerId(Long ownerId, PageRequest pageRequest) {
        return aircraftRepository.findByAirlineOwnerId(ownerId, pageRequest)
                .map(AircraftMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AircraftResponse> getAllAircrafts(PageRequest pageRequest) {
        return aircraftRepository.findAll(pageRequest).map(AircraftMapper::toResponse);
    }

    private Aircraft findAircraftById(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft not found with id: " + id));
    }

    private Airline getAirlineForOwner(Long ownerId) {
        return airlineRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Airline not found for owner id: " + ownerId));
    }

    private void validateCreate(AircraftRequest request) {
        if (request == null) {
            throw new BadRequestException("Aircraft request body is required");
        }

        if (request.getOwnerId() == null) {
            throw new BadRequestException("Owner id is required");
        }

        if (aircraftRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BadRequestException("Aircraft with code " + request.getCode() + " already exists");
        }
    }

    private void validateUpdate(Long id, AircraftRequest request) {
        if (request == null) {
            throw new BadRequestException("Aircraft request body is required");
        }

        if (request.getOwnerId() == null) {
            throw new BadRequestException("Owner id is required");
        }

        if (aircraftRepository.existsByCodeIgnoreCaseAndIdNot(request.getCode(), id)) {
            throw new BadRequestException("Aircraft with code " + request.getCode() + " already exists");
        }
    }
}

