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
    private String name;
    private String iataCode;
    private String icaoCode;

    private String alias;
    private String logoUrl;
    private AirlineStatus status;
    private String alliance;

    private Instant createdAt;
    private Instant updatedAt;

    private Long ownerId;
    private UserResponse owner;
    private String updatedBy;

    private CityResponse city;
    private Support support;
}
