package com.airline.client;

import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import com.airline.dto.response.AirportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "core-service")
public interface AirlineClient {

    @GetMapping("/api/airlines/{id}")
    AirlineResponse getAirlineById(@PathVariable Long id);

    @GetMapping("/api/aircraft/{id}")
    AircraftResponse getAircraftById(@PathVariable Long id);

    @GetMapping("/api/airports/{id}")
    AirportResponse getAirportById(@PathVariable Long id);
}
