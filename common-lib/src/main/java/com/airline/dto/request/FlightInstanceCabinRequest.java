package com.airline.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class FlightInstanceCabinRequest {

    @NotNull
    Long flightId;

    @NotNull(message = "Flight instance ID is required")
    Long flightInstanceId;

    @NotNull
    Long cabinClassId;

    @NotNull
    @Positive
    Double baseFare;

    @NotNull
    Double windowSurcharge;

    @NotNull
    Double aisleSurcharge;

    @NotNull
    @PositiveOrZero
    Double taxesAndFees;

    @NotNull
    @PositiveOrZero
    Double airlineFees;

    Double currentPrice;
    Boolean isActive;
}
