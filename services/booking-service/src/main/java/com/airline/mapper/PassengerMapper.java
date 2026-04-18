package com.airline.mapper;

import com.airline.dto.request.PassengerRequest;
import com.airline.dto.response.PassengerResponse;
import com.airline.entity.Passenger;

public class PassengerMapper {
    public static Passenger toEntity(PassengerRequest passengerRequest) {
        return Passenger
                .builder()
                .firstName(passengerRequest.getFirstName())
                .lastName(passengerRequest.getLastName())
                .dateOfBirth(passengerRequest.getDateOfBirth())
                .email(passengerRequest.getEmail())
                .phone(passengerRequest.getPhone())
                .gender(passengerRequest.getGender())
                .nationality(passengerRequest.getNationality())
                .build();
    }


    public static PassengerResponse toResponse(Passenger passenger) {
        return PassengerResponse
                .builder()
                .id(passenger.getId())
                .firstName(passenger.getFirstName())
                .lastName(passenger.getLastName())
                .email(passenger.getEmail())
                .phone(passenger.getPhone())
                .dateOfBirth(passenger.getDateOfBirth())
                .gender(passenger.getGender())
                .nationality(passenger.getNationality())
                .primaryUserId(passenger.getPrimaryUserId())
                .isActive(passenger.getIsActive())
                .age(passenger.getAge())
                .isAdult(passenger.isAdult())
                .fullName(passenger.getFullName())
                .createdAt(passenger.getCreatedAt())
                .updatedAt(passenger.getUpdatedAt())
                .build();
    }

}
