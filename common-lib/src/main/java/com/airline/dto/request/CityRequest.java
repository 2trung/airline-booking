package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.ZoneId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CityRequest {

    @NotBlank(message = "City name is required")
    @Size(max = 100)
    String name;

    @NotBlank(message = "City code is required")
    @Size(max = 10)
    String cityCode;

    @NotBlank(message = "Country code is required")
    @Size(max = 5)
    String countryCode;

    @NotBlank(message = "Country name is required")
    @Size(max = 100)
    String countryName;

    @Size(max = 10)
    String regionCode;

    String timeZoneId;

    public String getTimeZone(String timeZoneId) {
        try {
            ZoneId zoneId = ZoneId.of(timeZoneId);
            return "GMT" + zoneId.getRules().getOffset(Instant.now()).getId();
        } catch (Exception e) {
            return "";
        }
    }
}
