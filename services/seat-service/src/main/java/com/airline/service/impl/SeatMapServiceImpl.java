package com.airline.service.impl;

import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.SeatMapResponse;
import com.airline.entity.CabinClass;
import com.airline.entity.SeatMap;
import com.airline.mapper.SeatMapMapper;
import com.airline.repository.CabinClassRepository;
import com.airline.repository.SeatMapRepository;
import com.airline.service.SeatMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {

    private final SeatMapRepository seatMapRepository;
    private final CabinClassRepository cabinClassRepository;

    @Override
    @Transactional
    public SeatMapResponse createSeatMap(SeatMapRequest seatMapRequest) {
        log.info("Creating seat map for cabin class ID: {}", seatMapRequest.getCabinClassId());

        CabinClass cabinClass = findCabinClassByIdOrThrow(seatMapRequest.getCabinClassId());
        if (seatMapRepository.existsByAirlineIdAndCabinClassIdAndName(
                seatMapRequest.getAirlineId(),
                seatMapRequest.getCabinClassId(),
                seatMapRequest.getName()
        )) {
            throw new RuntimeException("Seat map already exists for cabin class ID: " + cabinClass.getId());
        }

        SeatMap seatMap = SeatMapMapper.toEntity(seatMapRequest, cabinClass);
        cabinClass.setSeatMap(seatMap);

        // todo: generate seat map

        SeatMap savedSeatMap = seatMapRepository.save(seatMap);
        log.info("Seat map created successfully with ID: {}", savedSeatMap.getId());
        return SeatMapMapper.toResponse(savedSeatMap);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatMapResponse getSeatMapById(Long id) {
        log.info("Fetching seat map by ID: {}", id);
        return SeatMapMapper.toResponse(findSeatMapByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public SeatMapResponse getSeatMapByCabinClassId(Long cabinClassId) {
        log.info("Fetching seat map by cabin class ID: {}", cabinClassId);
        SeatMap seatMap = seatMapRepository.findByCabinClass_Id(cabinClassId)
                .orElseThrow(() -> new RuntimeException("Seat map not found for cabin class ID: " + cabinClassId));
        return SeatMapMapper.toResponse(seatMap);
    }

    @Override
    @Transactional
    public SeatMapResponse updateSeatMap(Long id, SeatMapRequest seatMapRequest) {
        log.info("Updating seat map with ID: {}", id);

        SeatMap seatMap = findSeatMapByIdOrThrow(id);
        CabinClass oldCabinClass = seatMap.getCabinClass();

        Long requestedCabinClassId = seatMapRequest.getCabinClassId();
        CabinClass targetCabinClass = oldCabinClass;

        if (requestedCabinClassId != null && (oldCabinClass == null || !requestedCabinClassId.equals(oldCabinClass.getId()))) {
            targetCabinClass = findCabinClassByIdOrThrow(requestedCabinClassId);
        }

        if (targetCabinClass != null && seatMapRepository.existsByCabinClass_IdAndIdNot(targetCabinClass.getId(), id)) {
            throw new RuntimeException("Seat map already exists for cabin class ID: " + targetCabinClass.getId());
        }

        if (oldCabinClass != null && !oldCabinClass.getId().equals(targetCabinClass.getId())) {
            oldCabinClass.setSeatMap(null);
        }

        SeatMapMapper.updateEntityFromRequest(seatMap, seatMapRequest, targetCabinClass);
        if (targetCabinClass != null) {
            targetCabinClass.setSeatMap(seatMap);
        }

        SeatMap updatedSeatMap = seatMapRepository.save(seatMap);
        log.info("Seat map updated successfully with ID: {}", updatedSeatMap.getId());
        return SeatMapMapper.toResponse(updatedSeatMap);
    }

    @Override
    @Transactional
    public void deleteSeatMap(Long id) {
        log.info("Deleting seat map with ID: {}", id);

        SeatMap seatMap = findSeatMapByIdOrThrow(id);
        if (seatMap.getCabinClass() != null) {
            seatMap.getCabinClass().setSeatMap(null);
            seatMap.setCabinClass(null);
        }

        seatMapRepository.delete(seatMap);
        log.info("Seat map deleted successfully with ID: {}", id);
    }

    private SeatMap findSeatMapByIdOrThrow(Long id) {
        return seatMapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seat map not found with ID: " + id));
    }

    private CabinClass findCabinClassByIdOrThrow(Long cabinClassId) {
        if (cabinClassId == null) {
            throw new RuntimeException("Cabin class ID is required");
        }

        return cabinClassRepository.findById(cabinClassId)
                .orElseThrow(() -> new RuntimeException("Cabin class not found with ID: " + cabinClassId));
    }
}
