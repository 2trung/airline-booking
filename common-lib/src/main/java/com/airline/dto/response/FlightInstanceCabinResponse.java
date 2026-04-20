package com.airline.dto.response;

import com.airline.enums.CabinClassType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightInstanceCabinResponse {
    Long id;
    Long flightInstanceId;
    CabinClassType cabinClassType;
    CabinClassResponse cabinClass;
    @Builder.Default
    List<SeatInstanceResponse> seats = new ArrayList<>();
    @Builder.Default
    SeatMapResponse seatMap = new SeatMapResponse();
    Integer totalSeats;
    Integer bookedSeats;
    Integer availableSeats;
    Boolean isActive;
    Boolean canBook;
}
