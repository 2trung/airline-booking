package com.airline.dto.request;

import com.airline.embeddable.Address;
import com.airline.embeddable.GeoCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AirportRequest {

    @NotBlank(message = "IATA code is mandatory")
    @Size(min = 3, max = 3, message = "IATA code must be exactly 3 characters")
    String iataCode;

    @NotBlank(message = "Airport name is mandatory")
    String name;

    ZoneId timeZone;

    @Valid
    Address address;

    @NotNull(message = "City ID is mandatory")
    Long cityId;

    @Valid
    GeoCode geoCode;
}
