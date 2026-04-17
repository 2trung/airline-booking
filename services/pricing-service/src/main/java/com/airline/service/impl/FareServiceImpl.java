package com.airline.service.impl;

import com.airline.dto.request.FareRequest;
import com.airline.dto.response.FareResponse;
import com.airline.entity.Fare;
import com.airline.mapper.FareMapper;
import com.airline.repository.FareRepository;
import com.airline.service.FareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FareServiceImpl implements FareService {

    private final FareRepository fareRepository;
    private final FareMapper fareMapper;

    @Override
    @Transactional
    public FareResponse createFare(FareRequest fareRequest) {
        log.info("Creating fare for flight: {}, cabin class: {}", fareRequest.getFlightId(), fareRequest.getCabinClassId());
        if (fareRepository.existsByFlightIdAndCabinClassIdAndName(
                fareRequest.getFlightId(),
                fareRequest.getCabinClassId(),
                fareRequest.getName()
        )) {
            throw new RuntimeException("Fare already exists for flight: " + fareRequest.getFlightId() + ", cabin class: " + fareRequest.getCabinClassId());
        }
        Fare fare = fareMapper.toEntity(fareRequest);
        Fare savedFare = fareRepository.save(fare);
        log.info("Fare created with ID: {}", savedFare.getId());
        return fareMapper.toResponse(savedFare);
    }

    @Override
    @Transactional(readOnly = true)
    public FareResponse getFareById(Long fareId) {
        log.info("Fetching fare by ID: {}", fareId);
        Fare fare = fareRepository.findById(fareId)
                .orElseThrow(() -> new RuntimeException("Fare not found with ID: " + fareId));
        return fareMapper.toResponse(fare);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareResponse> getFaresByFlightIdAndCabinClassId(Long flightId, Long cabinClassId) {
        log.info("Fetching fares for flight: {}, cabin class: {}", flightId, cabinClassId);
        List<Fare> fares = fareRepository.findByFlightIdAndCabinClassId(flightId, cabinClassId);
        return fares.stream()
                .map(fareMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FareResponse updateFare(Long fareId, FareRequest fareRequest) {
        log.info("Updating fare with ID: {}", fareId);
        Fare fare = fareRepository.findById(fareId)
                .orElseThrow(() -> new RuntimeException("Fare not found with ID: " + fareId));

        if (fareRepository.existsByFlightIdAndCabinClassIdAndNameAndIdNot(
                fareRequest.getFlightId(),
                fareRequest.getCabinClassId(),
                fareRequest.getName(),
                fareId
        )) {
            throw new RuntimeException("Fare already exists for flight: " + fareRequest.getFlightId() + ", cabin class: " + fareRequest.getCabinClassId());
        }
        fareMapper.updateEntity(fare, fareRequest);
        Fare updatedFare = fareRepository.save(fare);
        log.info("Fare updated successfully with ID: {}", fareId);
        return fareMapper.toResponse(updatedFare);
    }

    @Override
    @Transactional
    public void deleteFare(Long fareId) {
        log.info("Deleting fare with ID: {}", fareId);
        if (!fareRepository.existsById(fareId)) {
            throw new RuntimeException("Fare not found with ID: " + fareId);
        }
        fareRepository.deleteById(fareId);
        log.info("Fare deleted successfully with ID: {}", fareId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareResponse> getFares() {
        log.info("Fetching all fares");
        List<Fare> fares = fareRepository.findAll();
        return fares.stream()
                .map(fareMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, FareResponse> getLowestFareByFlight(List<Long> flightIds, Long cabinClassId) {
        log.info("Fetching lowest fares for {} flights and cabin class: {}", flightIds.size(), cabinClassId);
        List<Fare> fares = fareRepository.findLowestFaresByFlightIds(flightIds, cabinClassId);

        Map<Long, FareResponse> lowestFaresMap = new HashMap<>();
        for (Fare fare : fares) {
            if (!lowestFaresMap.containsKey(fare.getFlightId())) {
                lowestFaresMap.put(fare.getFlightId(), fareMapper.toResponse(fare));
            }
        }

        log.info("Found lowest fares for {} flights", lowestFaresMap.size());
        return lowestFaresMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, FareResponse> getFaresByIds(List<Long> fareIds) {
        log.info("Fetching fares by IDs: {}", fareIds);
        List<Fare> fares = fareRepository.findByIdIn(fareIds);

        Map<Long, FareResponse> faresMap = fares.stream()
                .collect(Collectors.toMap(
                        Fare::getId,
                        fareMapper::toResponse
                ));

        log.info("Found {} fares", faresMap.size());
        return faresMap;
    }
}
