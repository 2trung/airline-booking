package com.airline.service;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface AircraftService {

    AircraftResponse createAircraft(AircraftRequest aircraftRequest);

    AircraftResponse updateAircraft(Long id, AircraftRequest aircraftRequest);

    void deleteAircraft(Long id);

    AircraftResponse getAircraftById(Long id);

    List<AircraftResponse> getAircraftsByOwnerId(Long ownerId);

    Page<AircraftResponse> getAircraftsByOwnerId(Long ownerId, PageRequest pageRequest);

    Page<AircraftResponse> getAllAircrafts(PageRequest pageRequest);

}
