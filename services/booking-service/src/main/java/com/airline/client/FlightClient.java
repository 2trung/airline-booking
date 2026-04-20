package com.airline.client;

import com.airline.dto.response.FlightResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fly-ops-service")
public interface FlightClient {

    @GetMapping("/api/flights/{id}")
    FlightResponse getFlightById(@PathVariable Long id);
}
