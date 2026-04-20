package com.airline.client;

import com.airline.dto.response.AirportResponse;
import com.airline.exception.AirportException;
import org.springframework.stereotype.Component;

@Component
public class LocationClientFallback implements LocationClient {

    @Override
    public AirportResponse getAirportById(Long id) throws AirportException {
        return null;
    }
}
