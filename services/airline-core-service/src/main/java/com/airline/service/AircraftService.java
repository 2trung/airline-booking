package com.airline.service;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AircraftService {

    AircraftResponse getAircraftById(Long id) throws ResourceNotFoundException;

    List<AircraftResponse> listAllAircraftsByOwner(Long ownerId);

    Page<AircraftResponse> searchAircrafts(String keyword, Pageable pageable);

    AircraftResponse createAircraft(AircraftRequest request,
                                    Long ownerId);

    AircraftResponse updateAircraft(Long id, AircraftRequest request, Long ownerId) throws ResourceNotFoundException;

    void deleteAircraft(Long id) throws ResourceNotFoundException;

}
