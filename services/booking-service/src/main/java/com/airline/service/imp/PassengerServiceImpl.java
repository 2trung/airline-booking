package com.airline.service.imp;

import com.airline.dto.request.PassengerRequest;
import com.airline.dto.response.PassengerResponse;
import com.airline.entity.Passenger;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.PassengerMapper;
import com.airline.repository.PassengerRepository;
import com.airline.service.PassengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassengerServiceImpl implements PassengerService {

    private final PassengerRepository passengerRepository;

    @Override
    @Transactional
    public PassengerResponse createPassenger(PassengerRequest request, Long userId)
            throws ResourceNotFoundException {
        Passenger passenger = PassengerMapper.toEntity(request);
        passenger.setPrimaryUserId(userId);
        Passenger saved = passengerRepository.save(passenger);
        return PassengerMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public Passenger findOrCreatePassengerEntity(
            PassengerRequest request, Long userId) {
        Optional<Passenger> existing = findExistingPassengerOptional(request);
        if (existing.isPresent()) {
            Passenger passenger = existing.get();
            PassengerMapper.updateEntityFromRequest(request, passenger);
            return passengerRepository.save(passenger);
        }

        Passenger newPassenger = PassengerMapper.toEntity(request);
        newPassenger.setPrimaryUserId(userId);
        return passengerRepository.save(newPassenger);
    }

    @Override
    public Passenger findExistingPassenger(PassengerRequest request) {
        return findExistingPassengerOptional(request).orElse(null);
    }

    @Override
    public boolean existsById(Long id) {
        return passengerRepository.existsById(id);
    }

    @Override
    public long count() {
        return passengerRepository.count();
    }

    private Optional<Passenger> findExistingPassengerOptional(PassengerRequest request) {
        if (request.getPassportNumber() != null && !request.getPassportNumber().isEmpty()) {
            Optional<Passenger> byPassport = passengerRepository.findByPassportNumber(
                    request.getPassportNumber());
            if (byPassport.isPresent()) {
                return byPassport;
            }
        }

        return passengerRepository.findByEmailAndPhoneAndDateOfBirth(
                request.getEmail(), request.getPhone(), request.getDateOfBirth());
    }
}
