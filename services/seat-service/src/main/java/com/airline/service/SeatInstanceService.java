package com.airline.service;

import java.util.List;

public interface SeatInstanceService {
    Double calculateSeatPrice(List<Long> seatInstanceIds);
}
