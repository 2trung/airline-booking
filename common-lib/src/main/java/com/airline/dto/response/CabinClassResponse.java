package com.airline.dto.response;

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
public class CabinClassResponse {
    Long id;
    String name;
    String code;
    String description;
    Long aircraftId;
    Integer displayOrder;
    Boolean isActive;
    Boolean isBookable;
    Integer typicalSeatPitch;
    Integer typicalSeatWidth;
    String seatType;
    Instant createdAt;
    Instant updatedAt;
    SeatMapResponse seatMap;
}
