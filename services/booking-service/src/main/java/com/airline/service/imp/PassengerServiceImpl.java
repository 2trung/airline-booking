package com.airline.service.imp;

import com.airline.dto.request.PassengerRequest;
import com.airline.entity.Passenger;
import com.airline.mapper.PassengerMapper;
import com.airline.repository.PassengerRepository;
import com.airline.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {
    private final PassengerRepository passengerRepository;
    @Override
    public Passenger createPassenger(PassengerRequest passengerRequest, Long userId) {
        Passenger passenger = PassengerMapper.toEntity(passengerRequest);
        passenger.setPrimaryUserId(userId);
        return passengerRepository.save(passenger);
    }

    private Optional<Passenger> findExistingPassenger(PassengerRequest passengerRequest) {
        return Optional.empty();
    }
}
