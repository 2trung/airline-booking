package com.airline.dto.response;

import com.airline.embeddable.Support;
import com.airline.enums.AirlineStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AirlineResponse {
    private Long id;

    private String iataCode;
    private String icaoCode;

    private String name;
    private String alias;
    private String country;

    private String logoUrl;
    private String website;

    private AirlineStatus status;
    private String alliance;

    private Long baggagePolicyId;

    private Long headquartersCityId;
//    private String headquartersCityName;

    private String supportEmail;
    private String supportPhone;
    private String supportHours;

    private Instant createdAt;
    private Instant updatedAt;

    private Long ownerId;
    private UserResponse owner;
    private Long updatedById;

//    private CityResponse headquartersCity;
    private Support support;
}
