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
public class FlightCabinAncillaryRequest {

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Cabin Class ID is required")
    Long cabinClassId;

    @NotNull(message = "Ancillary ID is required")
    Long ancillaryId;

    @NotNull(message = "Availability status is required")
    Boolean available;

    Integer maxQuantity;

    Double price;

    String currency;

    @NotNull(message = "Included in fare status is required")
    Boolean includedInFare;
}
