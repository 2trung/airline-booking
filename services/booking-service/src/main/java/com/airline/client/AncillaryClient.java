package com.airline.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ancillary-service")
public interface AncillaryClient {

    @GetMapping("/api/flight-cabin-ancillary/price")
    Double calculateAncillaryPrice(@RequestParam List<Long> ancillaryIds);

    @GetMapping("/api/flight-meals/price")
    Double calculateMealPrice(@RequestParam List<Long> mealIds);
}
