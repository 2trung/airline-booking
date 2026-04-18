package com.airline.service;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;

import java.util.List;

public interface AncillaryService {
    AncillaryResponse createAncillary(Long airlineId, AncillaryRequest ancillaryRequest);

    AncillaryResponse getAncillaryById(Long id) throws Exception;

    List<AncillaryResponse> getByAirlineId(Long airlineId);

    AncillaryResponse updateAncillary(Long id, AncillaryRequest ancillaryRequest);

    void deleteAncillary(Long id);
}
