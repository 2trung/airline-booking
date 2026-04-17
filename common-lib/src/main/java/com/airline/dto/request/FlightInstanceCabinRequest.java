package com.airline.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightInstanceCabinRequest {
    @NotNull(message = "Cabin class type is required")
    Long flightId;

    @NotNull(message = "Cabin class is required")
    Long flightInstanceId;

    @NotNull(message = "Cabin class is required")
    Long cabinClassId;

    @NotNull(message = "Base fare is required")
    @Positive(message = "Base fare must be a positive value")
    Double basFare;

    @NotNull()
    @Positive(message = "Window surcharge must be a positive value")
    Double windowSurcharge;

    @NotNull()
    @Positive(message = "Aisle surcharge must be a positive value")
    Double aisleSurcharge;

    Double currentPrice;
    Boolean isActive;
}
