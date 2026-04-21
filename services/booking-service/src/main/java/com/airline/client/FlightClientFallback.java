package com.airline.client;

import com.airline.dto.response.FlightInstanceResponse;
import com.airline.dto.response.FlightResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class FlightClientFallback implements FlightClient {

    @Override
    public FlightResponse getFlightById(Long id) {
        return null;
    }

    @Override
    public FlightInstanceResponse getFlightInstanceResponse(Long id) {
        return null;
    }

    @Override
    public Map<Long, FlightResponse> getFlightsByIds(List<Long> ids) {
        return Collections.emptyMap();
    }

    @Override
    public Map<Long, FlightInstanceResponse> getFlightInstancesByIds(List<Long> ids) {
        return Collections.emptyMap();
    }
}
