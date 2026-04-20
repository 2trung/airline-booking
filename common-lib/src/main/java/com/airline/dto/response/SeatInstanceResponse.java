package com.airline.dto.response;

import com.airline.enums.CabinClassType;
import com.airline.enums.SeatAvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SeatInstanceResponse {
    Long id;

    Long flightId;
    Long seatId;
    String seatNumber;
    String seatType;
    String seatPosition;

    SeatResponse seat;

    Double price;

    SeatAvailabilityStatus status;

    Long flightInstanceId;

    Boolean isBooked;

    Long flightCabinId;
    CabinClassType flightCabinClassType;

    String mealPreference;
    Double fare;

    Long version;
    Instant createdAt;
    Instant updatedAt;

    Boolean isAvailable;
    Boolean isOccupied;
    String seatCharacteristics;
}
