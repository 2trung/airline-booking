package com.airline.dto.response;

import com.airline.embeddable.Address;
import com.airline.embeddable.Analytics;
import com.airline.embeddable.GeoCode;
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
public class AirportResponse {

    Long id;
    String iataCode;
    String name;
    String detailedName;
    ZoneId timeZone;
    Address address;
    CityResponse city;
    GeoCode geoCode;
    Analytics analytics;
}
