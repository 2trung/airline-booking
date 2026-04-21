package com.airline.service;

import com.airline.dto.request.PassengerRequest;
import com.airline.dto.response.PassengerResponse;
import com.airline.entity.Passenger;
import com.airline.exception.ResourceNotFoundException;

public interface PassengerService {

    PassengerResponse createPassenger(PassengerRequest request, Long userId)
            throws ResourceNotFoundException;

    Passenger findOrCreatePassengerEntity(PassengerRequest request, Long userId);

    Passenger findExistingPassenger(PassengerRequest request);

    boolean existsById(Long id);

    long count();
}
