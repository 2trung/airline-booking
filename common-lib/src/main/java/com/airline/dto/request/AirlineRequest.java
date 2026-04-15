package com.airline.dto.request;

import com.airline.enums.AirlineStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirlineRequest {
    @NotBlank(message = "IATA code is required")
    @Size(min = 2, max = 2, message = "IATA code must be exactly 2 characters")
    private String iataCode;

    @NotBlank(message = "ICAO code is required")
    @Size(min = 3, max = 3, message = "ICAO code must be exactly 3 characters")
    private String icaoCode;

    @NotBlank(message = "Airline name is required")
    private String name;
    private String alias;

    private String logoUrl;

    private String website;
    private AirlineStatus status;
    private Long headquartersCityId;
    private String supportEmail;
    private String supportPhone;
    private String supportHours;
    private String alliance;
}
