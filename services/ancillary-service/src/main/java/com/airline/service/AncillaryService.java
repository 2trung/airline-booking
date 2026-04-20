package com.airline.service;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;
import com.airline.exception.ResourceNotFoundException;

import java.util.List;

public interface AncillaryService {
    AncillaryResponse create(Long userId, AncillaryRequest request) throws ResourceNotFoundException;

    AncillaryResponse getById(Long id) throws ResourceNotFoundException;

    List<AncillaryResponse> getAllByAirlineId(Long userId);

    AncillaryResponse update(Long id, AncillaryRequest request) throws ResourceNotFoundException;

    void delete(Long id);
}
