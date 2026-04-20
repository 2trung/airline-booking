package com.airline.client;


import com.airline.dto.response.AirportResponse;
import com.airline.exception.AirportException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service", fallback = LocationClientFallback.class)
public interface LocationClient {

    @GetMapping("/{id}")
    AirportResponse getAirportById(@PathVariable Long id) throws AirportException;
}
