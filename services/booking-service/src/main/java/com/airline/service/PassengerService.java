package com.airline.service;

import com.airline.dto.request.PassengerRequest;
import com.airline.entity.Passenger;

public interface PassengerService {
    Passenger createPassenger(PassengerRequest passengerRequest, Long userId);
}
