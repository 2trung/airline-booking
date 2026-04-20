package com.airline.service;

import com.airline.dto.request.SeatRequest;
import com.airline.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {
    void generateSeats(Long seatMapId) throws Exception;

    SeatResponse getSeatById(Long id);

    List<SeatResponse> getAll();

    SeatResponse updateSeat(Long id, SeatRequest request);
}
