package com.airline.service;

import com.airline.dto.request.SeatRequest;
import com.airline.dto.response.SeatResponse;

import java.util.List;

public interface SeatService {
    void generateSeats(Long seatMapId);

    SeatResponse updateSeats(Long seatId, SeatRequest seatRequest);

    List<SeatResponse> getAll();
}
