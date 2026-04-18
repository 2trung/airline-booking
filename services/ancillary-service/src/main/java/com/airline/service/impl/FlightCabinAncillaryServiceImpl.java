package com.airline.service.impl;

import com.airline.dto.request.FlightCabinAncillaryRequest;
import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.entity.Ancillary;
import com.airline.entity.FlightCabinAncillary;
import com.airline.enums.AncillaryType;
import com.airline.mapper.FlightCabinAncillaryMapper;
import com.airline.mapper.InsuranceCoverageMapper;
import com.airline.repository.AncillaryRepository;
import com.airline.repository.FlightCabinAncillaryRepository;
import com.airline.repository.InsuranceCoverageRepository;
import com.airline.service.FlightCabinAncillaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightCabinAncillaryServiceImpl implements FlightCabinAncillaryService {

    private final FlightCabinAncillaryRepository flightCabinAncillaryRepository;
    private final AncillaryRepository ancillaryRepository;
    private final InsuranceCoverageRepository insuranceCoverageRepository;

    @Override
    public FlightCabinAncillaryResponse createFlightCabinAncillary(FlightCabinAncillaryRequest flightCabinAncillaryRequest) {
        Ancillary ancillary = ancillaryRepository.findById(flightCabinAncillaryRequest.getAncillaryId())
                .orElseThrow(() -> new RuntimeException("Ancillary not found with id: " + flightCabinAncillaryRequest.getAncillaryId()));

        FlightCabinAncillary flightCabinAncillary = FlightCabinAncillary.builder()
                .flightId(flightCabinAncillaryRequest.getFlightId())
                .cabinClassId(flightCabinAncillaryRequest.getCabinClassId())
                .ancillary(ancillary)
                .available(flightCabinAncillaryRequest.getAvailable())
                .maxQuantity(flightCabinAncillaryRequest.getMaxQuantity())
                .price(flightCabinAncillaryRequest.getPrice())
                .includedInFare(flightCabinAncillaryRequest.getIncludedInFare())
                .build();

        FlightCabinAncillary saved = flightCabinAncillaryRepository.save(flightCabinAncillary);
        return toResponse(saved);
    }

    @Override
    public FlightCabinAncillaryResponse getById(Long id) {
        FlightCabinAncillary flightCabinAncillary = flightCabinAncillaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight cabin ancillary not found with id: " + id));
        return toResponse(flightCabinAncillary);
    }

    @Override
    public void deleteById(Long id) {
        FlightCabinAncillary flightCabinAncillary = flightCabinAncillaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight cabin ancillary not found with id: " + id));
        flightCabinAncillaryRepository.delete(flightCabinAncillary);
    }

    @Override
    public List<FlightCabinAncillaryResponse> getByFlightAndCabinClass(Long flightId, Long cabinClassId) {
        return flightCabinAncillaryRepository.findByFlightIdAndCabinClassId(flightId, cabinClassId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public List<FlightCabinAncillaryResponse> getAllByIds(List<Long> ids) {
        return flightCabinAncillaryRepository.findAllById(ids)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FlightCabinAncillaryResponse getByFlightIdAndCabinClassIdAndType(Long flightId, Long cabinClassId, AncillaryType type) {
        FlightCabinAncillary flightCabinAncillary = flightCabinAncillaryRepository
                .findByFlightIdAndCabinClassIdAndAncillary_Type(flightId, cabinClassId, type);
        return toResponse(flightCabinAncillary);
    }

    @Override
    public List<FlightCabinAncillaryResponse> getAllByFlightIdAndCabinClassIdAndType(Long flightId, Long cabinClassId, AncillaryType type) {
        return flightCabinAncillaryRepository.findAllByFlightIdAndCabinClassIdAndAncillary_Type(flightId, cabinClassId, type)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public FlightCabinAncillaryResponse update(Long id, FlightCabinAncillaryRequest flightCabinAncillaryRequest) {
        FlightCabinAncillary existing = flightCabinAncillaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight cabin ancillary not found with id: " + id));

        if (flightCabinAncillaryRequest.getAvailable() != null) {
            existing.setAvailable(flightCabinAncillaryRequest.getAvailable());
        }
        if (flightCabinAncillaryRequest.getMaxQuantity() != null) {
            existing.setMaxQuantity(flightCabinAncillaryRequest.getMaxQuantity());
        }
        if (flightCabinAncillaryRequest.getIncludedInFare() != null) {
            existing.setIncludedInFare(flightCabinAncillaryRequest.getIncludedInFare());
        }
        if (flightCabinAncillaryRequest.getPrice() != null) {
            existing.setPrice(flightCabinAncillaryRequest.getPrice());
        }

        FlightCabinAncillary updated = flightCabinAncillaryRepository.save(existing);
        return toResponse(updated);
    }

    @Override
    public Double calculateAncillaryPrice(List<Long> ancillaryIds) {
        if (ancillaryIds == null || ancillaryIds.isEmpty()) {
            return 0.0;
        }

        return flightCabinAncillaryRepository.findAllById(ancillaryIds)
                .stream()
                .map(FlightCabinAncillary::getPrice)
                .map(price -> price != null ? price : 0.0)
                .reduce(0.0, Double::sum);
    }

    private FlightCabinAncillaryResponse toResponse(FlightCabinAncillary flightCabinAncillary) {
        List<InsuranceCoverageResponse> coverages = insuranceCoverageRepository.findByAncillaryId(flightCabinAncillary.getId())
                .stream()
                .map(InsuranceCoverageMapper::toResponse)
                .toList();
        return FlightCabinAncillaryMapper.toResponse(flightCabinAncillary, coverages);
    }
}

