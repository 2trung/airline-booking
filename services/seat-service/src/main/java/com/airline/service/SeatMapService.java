package com.airline.service;

import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.SeatMapResponse;

public interface SeatMapService {
    SeatMapResponse createSeatMap(SeatMapRequest seatMapRequest);

    SeatMapResponse getSeatMapById(Long id);

    SeatMapResponse getSeatMapByCabinClassId(Long cabinClassId);

    SeatMapResponse updateSeatMap(Long id, SeatMapRequest seatMapRequest);

    void deleteSeatMap(Long id);
}
