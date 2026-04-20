package com.airline.service;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.exception.ResourceNotFoundException;

import java.util.List;

public interface AircraftService {

    AircraftResponse getAircraftById(Long id) throws ResourceNotFoundException;

    List<AircraftResponse> listAllAircraftsByOwner(Long ownerId);

    AircraftResponse createAircraft(AircraftRequest request,
                                    Long ownerId);

    AircraftResponse updateAircraft(Long id, AircraftRequest request, Long ownerId) throws ResourceNotFoundException;

    void deleteAircraft(Long id) throws ResourceNotFoundException;

}
