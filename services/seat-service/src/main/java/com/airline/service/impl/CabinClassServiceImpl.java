package com.airline.service.impl;

import com.airline.dto.request.CabinClassRequest;
import com.airline.dto.response.CabinClassResponse;
import com.airline.entity.CabinClass;
import com.airline.enums.CabinClassType;
import com.airline.mapper.CabinClassMapper;
import com.airline.repository.CabinClassRepository;
import com.airline.service.CabinClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CabinClassServiceImpl implements CabinClassService {

    private final CabinClassRepository cabinClassRepository;

    @Override
    @Transactional
    public CabinClassResponse createCabinClass(CabinClassRequest cabinClassRequest) {
        log.info("Creating cabin class: {} for aircraft: {}",
                cabinClassRequest.getName(), cabinClassRequest.getAircraftId());

        if (cabinClassRepository.existsByCodeAndAirCraftId(cabinClassRequest.getCode(), cabinClassRequest.getAircraftId())) {
            throw new RuntimeException("CabinClass already exists with this code");
        }
        CabinClass cabinClass = CabinClassMapper.toEntity(cabinClassRequest);
        CabinClass savedCabinClass = cabinClassRepository.save(cabinClass);

        log.info("Cabin class created successfully with ID: {}", savedCabinClass.getId());
        return CabinClassMapper.toResponse(savedCabinClass);
    }

    @Override
    @Transactional(readOnly = true)
    public CabinClassResponse getCabinClassById(Long id) {
        log.info("Fetching cabin class with ID: {}", id);

        CabinClass cabinClass = cabinClassRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cabin class not found with ID: {}", id);
                    return new RuntimeException("Cabin class not found with ID: " + id);
                });

        return CabinClassMapper.toResponse(cabinClass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CabinClassResponse> getAllCabinClassesByAircraftId(Long aircraftId) {
        log.info("Fetching all cabin classes for aircraft ID: {}", aircraftId);

        List<CabinClass> cabinClasses = cabinClassRepository.findByAirCraftIdOrderByDisplayOrderAsc(aircraftId);

        log.info("Found {} cabin classes for aircraft ID: {}", cabinClasses.size(), aircraftId);
        return cabinClasses.stream()
                .map(CabinClassMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CabinClassResponse getByAircraftIdAndName(Long aircraftId, String name) {
        log.info("Fetching cabin class for aircraft ID: {} and name: {}", aircraftId, name);

        CabinClassType cabinClassType;
        try {
            cabinClassType = CabinClassType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid cabin class name: {}", name);
            throw new RuntimeException("Invalid cabin class name: " + name);
        }

        CabinClass cabinClass = cabinClassRepository.findByAirCraftIdAndName(aircraftId, cabinClassType)
                .orElseThrow(() -> {
                    log.error("Cabin class not found for aircraft ID: {} and name: {}", aircraftId, name);
                    return new RuntimeException("Cabin class not found for aircraft ID: " + aircraftId + " and name: " + name);
                });

        return CabinClassMapper.toResponse(cabinClass);
    }

    @Override
    @Transactional
    public CabinClassResponse updateCabinClass(Long id, CabinClassRequest cabinClassRequest) {
        log.info("Updating cabin class with ID: {}", id);

        CabinClass cabinClass = cabinClassRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cabin class not found with ID: {}", id);
                    return new RuntimeException("Cabin class not found with ID: " + id);
                });

        if (cabinClassRepository.existsByCodeAndAirCraftIdAndIdNot(
                cabinClass.getCode(), cabinClass.getAirCraftId(), cabinClass.getId()
        )) {
            throw new RuntimeException("Cabin class already exists with this code");
        }

        CabinClassMapper.updateEntityFromRequest(cabinClass, cabinClassRequest);
        CabinClass updatedCabinClass = cabinClassRepository.save(cabinClass);

        log.info("Cabin class updated successfully with ID: {}", updatedCabinClass.getId());
        return CabinClassMapper.toResponse(updatedCabinClass);
    }

    @Override
    @Transactional
    public void deleteCabinClass(Long id) {
        log.info("Deleting cabin class with ID: {}", id);

        if (!cabinClassRepository.existsById(id)) {
            log.error("Cabin class not found with ID: {}", id);
            throw new RuntimeException("Cabin class not found with ID: " + id);
        }

        cabinClassRepository.deleteById(id);
        log.info("Cabin class deleted successfully with ID: {}", id);
    }
}
