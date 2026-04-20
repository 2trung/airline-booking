package com.airline.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SeatInstanceRequest {

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Flight instance ID is required")
    Long flightInstanceId;

    @NotNull(message = "Flight instance cabin ID is required")
    Long flightInstanceCabinId;

    @NotNull(message = "Seat ID is required")
    Long seatId;

    String status;
    String mealPreference;
    Double fare;
    Long flightScheduleId;
}
