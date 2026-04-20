package com.airline.client;

import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import org.springframework.stereotype.Component;

@Component
public class AirlineClientFallback implements AirlineClient {

    @Override
    public AirlineResponse getAirlineByOwner(Long userId) {
        return null;
    }

    @Override
    public AircraftResponse getAircraftById(Long id) {
        return null;
    }
}
