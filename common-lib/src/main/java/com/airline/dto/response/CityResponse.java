package com.airline.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CityResponse {

    Long id;
    String name;
    String cityCode;
    String countryCode;
    String countryName;
    String regionCode;
    String timeZoneOffset;
}
