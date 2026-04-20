package com.airline.dto.request;

import com.airline.enums.AirlineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AirlineRequest {
    @NotBlank
    @Size(min = 2, max = 2, message = "IATA code must be exactly 2 characters")
    String iataCode;

    @NotBlank
    @Size(min = 3, max = 3, message = "ICAO code must be exactly 3 characters")
    String icaoCode;

    @NotBlank
    String name;

    String alias;

    @NotBlank
    String country;

    String logoUrl;

    String website;

    AirlineStatus status;

    String alliance;

    Long headquartersCityId;

    String supportEmail;
    String supportPhone;
    String supportHours;
}
