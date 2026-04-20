package com.airline.service.impl;

import com.airline.clients.AirlineClient;
import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.SeatMapResponse;
import com.airline.entity.CabinClass;
import com.airline.entity.SeatMap;
import com.airline.mapper.SeatMapMapper;
import com.airline.repository.CabinClassRepository;
import com.airline.repository.SeatMapRepository;
import com.airline.service.SeatMapService;
import com.airline.service.SeatService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {

    private final SeatMapRepository seatMapRepository;
    private final CabinClassRepository cabinClassRepository;
    private final AirlineClient airlineClient;
    private final SeatService seatService;

    @Override
    public SeatMapResponse createSeatMap(Long userId, SeatMapRequest request) throws Exception {
        Long airlineId= getAirlineForUser(userId);

        CabinClass cabinClass = cabinClassRepository.findById(request.getCabinClassId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cabin class not found with id: " + request.getCabinClassId()));


        if (seatMapRepository.existsByAirlineIdAndCabinClassIdAndName(
                airlineId, request.getCabinClassId(), request.getName())) {
            throw new IllegalArgumentException(
                    "Seat map with name '" + request.getName() + "' already exists for this airline and cabin class");
        }

        SeatMap seatMap = SeatMapMapper.toEntity(request, cabinClass);
        seatMap.setAirlineId(airlineId);
        SeatMap savedSeatMap = seatMapRepository.save(seatMap);

//      generate seats for seatMap
        seatService.generateSeats(savedSeatMap.getId());
        return SeatMapMapper.toResponse(savedSeatMap);
    }



    @Override
    public List<SeatMapResponse> createSeatMaps(Long userId, List<SeatMapRequest> requests) throws Exception {
        Long airlineId = getAirlineForUser(userId);

        List<SeatMap> toSave = requests.stream()
                .filter(req -> !seatMapRepository.existsByAirlineIdAndCabinClassIdAndName(
                        airlineId, req.getCabinClassId(), req.getName()))
                .map(req -> {
                    CabinClass cabinClass = cabinClassRepository.findById(req.getCabinClassId())
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Cabin class not found with id: " + req.getCabinClassId()));
                    SeatMap seatMap = SeatMapMapper.toEntity(req, cabinClass);
                    seatMap.setAirlineId(airlineId);
                    return seatMap;
                })
                .collect(Collectors.toList());

        List<SeatMap> saved = seatMapRepository.saveAll(toSave);

        for (SeatMap seatMap : saved) {
            seatService.generateSeats(seatMap.getId());
        }

        return saved.stream()
                .map(SeatMapMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SeatMapResponse getSeatMapById(Long id) {
        SeatMap seatMap = seatMapRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Seat map not found with id: " + id));
        return SeatMapMapper.toResponse(seatMap);
    }



    @Override
    @Transactional(readOnly = true)
    public SeatMapResponse getSeatMapsByCabinClass(Long cabinClassId) {


        cabinClassRepository.findById(cabinClassId).orElseThrow(
                ()-> new EntityNotFoundException(
                        "CabinClass not found with id: " + cabinClassId)
        );

        SeatMap seatMap = seatMapRepository.findByCabinClassId(cabinClassId);

        if(seatMap==null){
            throw new EntityNotFoundException("seat map not found with id cabin class id: " + cabinClassId);
        }

        return SeatMapMapper.toResponse(seatMap);
    }


    @Override
    public SeatMapResponse updateSeatMap(Long userId, Long id, SeatMapRequest request) {
        Long airlineId= getAirlineForUser(userId);
        SeatMap existing = seatMapRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seat map not found with id: " + id));

        if (seatMapRepository.existsByAirlineIdAndNameAndIdNot(airlineId, request.getName(), id)) {
            throw new IllegalArgumentException(
                    "Seat map with name '" + request.getName() + "' already exists for this airline");
        }

        SeatMapMapper.updateEntity(request, existing);
        SeatMap saved = seatMapRepository.save(existing);
        return SeatMapMapper.toResponse(saved);
    }

    @Override
    public void deleteSeatMap(Long id) throws Exception {
        if (!seatMapRepository.existsById(id)) {
            throw new Exception("Seat map not found with id: " + id);
        }
        seatMapRepository.deleteById(id);
    }


    private Long getAirlineForUser(Long userId) {
        try {
            AirlineResponse airline = airlineClient.getAirlineByOwner(userId);
            return airline.getId();
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("No airline found for user: " + userId);
        } catch (FeignException e) {
            throw new RuntimeException("Failed to fetch airline from airline-core-service: " + e.getMessage(), e);
        }
    }
}
