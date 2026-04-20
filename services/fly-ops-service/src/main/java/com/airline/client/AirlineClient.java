package com.airline.client;

import com.airline.dto.response.AircraftResponse;
import com.airline.dto.response.AirlineResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "airline-core-service", fallback = AirlineClientFallback.class)
public interface AirlineClient {

    @GetMapping("/api/airlines/admin")
    AirlineResponse getAirlineByOwner(@RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/airlines/{airlineId}")
    AirlineResponse getAirlineById(@PathVariable Long airlineId);

    @GetMapping("/api/aircrafts/{id}")
    AircraftResponse getAircraftById(@PathVariable("id") Long id);

    @GetMapping("/api/airlines/by-iata")
    List<AirlineResponse> getAirlinesByIataCodes(@RequestParam("codes") List<String> codes);

    @GetMapping("/api/airlines/by-alliance")
    List<AirlineResponse> getAirlinesByAlliance(@RequestParam("alliance") String alliance);
}
