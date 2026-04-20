package com.airline.client;

import com.airline.dto.response.FareResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "pricing-service", fallbackFactory = PricingClientFallbackFactory.class)
public interface PricingClient {

    @PostMapping("/api/fares/search")
    Map<Long, FareResponse> getLowestFarePerFlight(@RequestBody List<Long> flightIds, @RequestParam("cabinClassId") Long cabinClassId);

    @GetMapping("/api/fares/lowest/flight/{flightId}/cabin-class/{cabinClassId}")
    FareResponse getLowestFareForFlightAndCabinClass(@PathVariable Long flightId, @PathVariable Long cabinClassId);
}
