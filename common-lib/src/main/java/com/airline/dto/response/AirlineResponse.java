package com.airline.dto.response;

import com.airline.embeddable.Support;
import com.airline.enums.AirlineStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AirlineResponse {
    Long id;

    String iataCode;
    String icaoCode;

    String name;
    String alias;
    String country;

    String logoUrl;
    String website;

    AirlineStatus status;
    String alliance;

//    Long baggagePolicyId;

    Long headquartersCityId;
//    String headquartersCityName;
//
//    String supportEmail;
//    String supportPhone;
//    String supportHours;

    Instant createdAt;
    Instant updatedAt;

    Long ownerId;
    UserResponse owner;
    Long updatedById;

//    CityResponse headquartersCity;
    Support support;
}
