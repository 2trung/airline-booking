package com.airline.service.impl;

import com.airline.dto.request.SeatInstanceRequest;
import com.airline.dto.response.SeatInstanceResponse;
import com.airline.entity.FlightInstanceCabin;
import com.airline.entity.Seat;
import com.airline.entity.SeatInstance;
import com.airline.enums.SeatAvailabilityStatus;
import com.airline.mapper.SeatInstanceMapper;
import com.airline.repository.FlightInstanceCabinRepository;
import com.airline.repository.SeatInstanceRepository;
import com.airline.repository.SeatRepository;
import com.airline.service.SeatInstanceService;
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
public class SeatInstanceServiceImpl implements SeatInstanceService {

    private final SeatInstanceRepository seatInstanceRepository;
    private final SeatRepository seatRepository;
    private final FlightInstanceCabinRepository flightInstanceCabinRepository;

    @Override
    public SeatInstanceResponse createSeatInstance(SeatInstanceRequest request) {
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Seat not found with id: " + request.getSeatId()));

        FlightInstanceCabin fic = null;
        if (request.getFlightInstanceCabinId() != null) {
            fic = flightInstanceCabinRepository.findById(request.getFlightInstanceCabinId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Flight instance cabin not found with id: " + request.getFlightInstanceCabinId()));
        }

        SeatInstance seatInstance = SeatInstanceMapper.toEntity(request, seat, fic);
        SeatInstance saved = seatInstanceRepository.save(seatInstance);
        return SeatInstanceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SeatInstanceResponse getSeatInstanceById(Long id) {
        SeatInstance si = seatInstanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Seat instance not found with id: " + id));
        return SeatInstanceMapper.toResponse(si);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatInstanceResponse> getSeatInstancesByFlightId(Long flightId) {
        return seatInstanceRepository.findByFlightId(flightId).stream()
                .map(SeatInstanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SeatInstanceResponse> getAvailableSeatsByFlightId(Long flightId) {
        return seatInstanceRepository.findAvailableByFlightId(flightId).stream()
                .map(SeatInstanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeatInstanceResponse> getAllByIds(List<Long> Ids) {
        List<SeatInstance> seatInstances = seatInstanceRepository.findAllById(Ids);
        return seatInstances.stream().map(
                SeatInstanceMapper::toResponse
        ).collect(Collectors.toList());
    }

    @Override
    public SeatInstanceResponse updateSeatInstanceStatus(Long id, SeatAvailabilityStatus status) {
        SeatInstance si = seatInstanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new EntityNotFoundException("Seat instance not found with id: " + id));

        if (status == SeatAvailabilityStatus.AVAILABLE) {
            si.setAvailable(true);
            si.setBooked(false);
        } else if (status == SeatAvailabilityStatus.BOOKED) {
            si.setAvailable(false);
            si.setBooked(true);
        }
        si.setStatus(status);

        SeatInstance saved = seatInstanceRepository.save(si);
        return SeatInstanceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAvailableByFlightId(Long flightId) {
        return seatInstanceRepository.countAvailableByFlightId(flightId);
    }

    @Override
    public Double calculateSeatPrice(List<Long> seatInstanceIds) {
        List<SeatInstance> seatInstances = seatInstanceRepository.findAllById(seatInstanceIds);
        double total=0.0;
        for (SeatInstance si : seatInstances) {

            double seatPremium = si.getPremiumSurcharge() != null
                    ? si.getPremiumSurcharge()
                    : 0.0;

            total+=seatPremium;

        }
        return total;
    }
}
