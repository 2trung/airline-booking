package com.airline.client;

import com.airline.dto.response.AirportResponse;
import org.springframework.stereotype.Component;

@Component
public class LocationClientFallback implements LocationClient {

    @Override
    public AirportResponse getAirportById(Long id) {
        return null;
    }
}
