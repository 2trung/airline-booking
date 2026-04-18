package com.airline.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class FlightCabinAncillaryRequest {

    @NotNull(message = "Flight id is required")
    Long flightId;

    @NotNull(message = "Cabin class id is required")
    Long cabinClassId;

    @NotNull(message = "Ancillary id is required")
    Long ancillaryId;

    @NotNull(message = "Availability status is required")
    Boolean available;

    Integer maxQuantity;

    Double price;

    @NotNull(message = "Included in fare status is required")
    Boolean includedInFare;
}
