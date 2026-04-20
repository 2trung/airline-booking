package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SeatMapResponse {
    Long id;
    String name;
    Integer totalRows;

    Long airlineId;
    String airlineName;
    String airlineCode;

    Long cabinClassId;
    String cabinClassName;
    String cabinClassCode;

    Integer totalSeats;
    Integer availableSeats;
    Integer occupiedSeats;

    List<SeatResponse> seats;

    Integer windowSeats;
    Integer aisleSeats;
    Integer middleSeats;
    Integer premiumSeats;
    Integer emergencyExitSeats;

    Integer leftSeatsPerRow;
    Integer rightSeatsPerRow;
}
