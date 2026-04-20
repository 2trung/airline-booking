package com.airline.service;

import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.SeatMapResponse;

import java.util.List;

public interface SeatMapService {
    SeatMapResponse createSeatMap(Long userId, SeatMapRequest request) throws Exception;

    List<SeatMapResponse> createSeatMaps(Long userId, List<SeatMapRequest> requests) throws Exception;

    SeatMapResponse getSeatMapById(Long id);

    SeatMapResponse getSeatMapsByCabinClass(Long cabinClassId);

    SeatMapResponse updateSeatMap(Long userId, Long id, SeatMapRequest request);

    void deleteSeatMap(Long id) throws Exception;
}
