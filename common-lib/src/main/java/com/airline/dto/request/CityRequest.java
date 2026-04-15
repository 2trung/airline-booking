package com.airline.dto.request;

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
public class CityRequest {

    @NotBlank(message = "City name is required")
    @Size(max = 100, message = "City name must be less than 100 characters")
    private String name;

    @NotBlank(message = "City code is required")
    @Size(max = 5, message = "City code must be less than 5 characters")
    private String cityCode;
    @NotBlank(message = "Country code is required")
    @Size(max = 10, message = "Country code must be less than 10 characters")
    private String countryCode;

    @NotBlank(message = "Country name is required")
    @Size(max = 100, message = "Country name must be less than 100 characters")
    private String countryName;

    @Size(max = 10, message = "Region code must be less than 10 characters")
    private String regionCode;

    @Size(max = 50, message = "Time zone must be less than 50 characters")
    private String timeZone;
}
