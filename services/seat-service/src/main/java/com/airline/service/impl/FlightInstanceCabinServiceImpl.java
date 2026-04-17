package com.airline.service.impl;

import com.airline.dto.request.FlightInstanceCabinRequest;
import com.airline.dto.response.FlightInstanceCabinResponse;
import com.airline.entity.CabinClass;
import com.airline.entity.FlightInstanceCabin;
import com.airline.entity.SeatInstance;
import com.airline.entity.SeatMap;
import com.airline.enums.SeatAvailabilityStatus;
import com.airline.enums.SeatType;
import com.airline.mapper.FlightInstanceCabinMapper;
import com.airline.repository.CabinClassRepository;
import com.airline.repository.FlightInstanceCabinRepository;
import com.airline.repository.SeatInstanceRepository;
import com.airline.repository.SeatMapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightInstanceCabinServiceImpl implements com.airline.service.FlightInstanceCabinService {
    private final CabinClassRepository cabinClassRepository;
    private final SeatMapRepository seatMapRepository;
    private final FlightInstanceCabinRepository flightInstanceCabinRepository;
    private final SeatInstanceRepository seatInstanceRepository;

    @Override
    public FlightInstanceCabinResponse createFlightInstanceCabin(FlightInstanceCabinRequest flightInstanceCabinRequest) {
        CabinClass cabinClass = cabinClassRepository.findById(flightInstanceCabinRequest.getCabinClassId())
                .orElseThrow(() -> new RuntimeException("Cabin class not found with id: " + flightInstanceCabinRequest.getCabinClassId()));

        SeatMap seatMap = seatMapRepository.findByCabinClass_Id(cabinClass.getId())
                .orElseThrow(() -> new RuntimeException("Seat map not found for cabin class id: " + cabinClass.getId()));

        if (seatMap.getSeats() == null || seatMap.getSeats().isEmpty()) {
            throw new RuntimeException("Seats not found for seat map id: " + seatMap.getId());
        }

        int totalSeats = seatMap.getSeats().size();

        FlightInstanceCabin flightInstanceCabin = FlightInstanceCabin
                .builder()
                .flightInstanceId(flightInstanceCabinRequest.getFlightInstanceId())
                .cabinClass(cabinClass)
                .totalSeats(totalSeats)
                .bookedSeats(0)
                .build();

        FlightInstanceCabin savedFlightInstanceCabin = flightInstanceCabinRepository.save(flightInstanceCabin);
        List<SeatInstance> seatInstances = seatMap.getSeats().stream()
                .map(seat -> SeatInstance
                        .builder()
                        .flightId(flightInstanceCabinRequest.getFlightInstanceId())
                        .status(SeatAvailabilityStatus.AVAILABLE)
                        .flightInstanceId(flightInstanceCabinRequest.getFlightInstanceId())
                        .flightInstanceCabin(savedFlightInstanceCabin)
                        .seat(seat)
                        .isAvailable(true)
                        .isBooked(false)
                        .premiumSuperCharge(getPremiumInstanceCabin(seat.getSeatType(), 1000.0, 500.0))
                        .build())
                .toList();

        seatInstanceRepository.saveAll(seatInstances);
        savedFlightInstanceCabin.setSeatInstances(seatInstances);

        return FlightInstanceCabinMapper.toResponse(savedFlightInstanceCabin);
    }

    @Override
    public FlightInstanceCabinResponse getFlightInstanceCabinById(Long id) {
        FlightInstanceCabin flightInstanceCabin = flightInstanceCabinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight instance cabin not found with id: " + id));

        return FlightInstanceCabinMapper.toResponse(flightInstanceCabin);
    }

    @Override
    public Page<FlightInstanceCabinResponse> getByFlightInstanceId(Long flightInstanceId, Pageable pageable) {
        return flightInstanceCabinRepository.findByFlightInstanceId(flightInstanceId, pageable)
                .map(FlightInstanceCabinMapper::toResponse);
    }

    @Override
    public FlightInstanceCabinResponse getByFlightInstanceIdAndCabinClassId(Long flightInstanceId, Long cabinClassId) {

        FlightInstanceCabin flightInstanceCabin = flightInstanceCabinRepository.findByFlightInstanceIdAndCabinClassId(flightInstanceId, cabinClassId);
        if (flightInstanceCabin == null) {
            throw new RuntimeException("Flight instance cabin not found for flight instance id: " + flightInstanceId + " and cabin class id: " + cabinClassId);
        }
        return FlightInstanceCabinMapper.toResponse(flightInstanceCabin);
    }

    @Override
    public FlightInstanceCabinResponse updateFlightInstanceCabin(Long id, FlightInstanceCabinRequest flightInstanceCabinRequest) {
        FlightInstanceCabin flightInstanceCabin = flightInstanceCabinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight instance cabin not found with id: " + id));

        if (flightInstanceCabinRequest.getCabinClassId() != null) {
            CabinClass cabinClass = cabinClassRepository.findById(flightInstanceCabinRequest.getCabinClassId())
                    .orElseThrow(() -> new RuntimeException("Cabin class not found with id: " + flightInstanceCabinRequest.getCabinClassId()));
            flightInstanceCabin.setCabinClass(cabinClass);
        }
        FlightInstanceCabin updatedFlightInstanceCabin = flightInstanceCabinRepository.save(flightInstanceCabin);
        return FlightInstanceCabinMapper.toResponse(updatedFlightInstanceCabin);

    }

    @Override
    public void deleteFlightInstanceCabin(Long id) {
        FlightInstanceCabin flightInstanceCabin = flightInstanceCabinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight instance cabin not found with id: " + id));

        flightInstanceCabinRepository.delete(flightInstanceCabin);

    }

    private Double getPremiumInstanceCabin(SeatType seatType, Double windowSuperChange, Double aisleSuperChange) {
        return switch (seatType) {
            case WINDOW -> windowSuperChange;
            case AISLE -> aisleSuperChange;
            default -> 0.0;
        };
    }
}
