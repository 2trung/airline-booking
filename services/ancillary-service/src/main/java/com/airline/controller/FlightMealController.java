package com.airline.controller;

import com.airline.dto.request.FlightMealRequest;
import com.airline.dto.response.FlightMealResponse;
import com.airline.service.FlightMealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flight-meals")
@RequiredArgsConstructor
public class FlightMealController {

    private final FlightMealService flightMealService;

    @PostMapping
    public ResponseEntity<FlightMealResponse> createFlightMeal(@Valid @RequestBody FlightMealRequest flightMealRequest) {
        log.info("REST request to create flight meal for flight ID: {}", flightMealRequest.getFlightId());
        FlightMealResponse response = flightMealService.createFlightMeal(flightMealRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{flightMealId}")
    public ResponseEntity<FlightMealResponse> getFlightMealById(@PathVariable Long flightMealId) {
        log.info("REST request to get flight meal by ID: {}", flightMealId);
        FlightMealResponse response = flightMealService.getFlightMealById(flightMealId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{flightMealId}")
    public ResponseEntity<FlightMealResponse> updateFlightMeal(
            @PathVariable Long flightMealId,
            @Valid @RequestBody FlightMealRequest flightMealRequest) {
        log.info("REST request to update flight meal with ID: {}", flightMealId);
        FlightMealResponse response = flightMealService.updateFlightMeal(flightMealId, flightMealRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{flightMealId}")
    public ResponseEntity<Void> deleteFlightMeal(@PathVariable Long flightMealId) {
        log.info("REST request to delete flight meal with ID: {}", flightMealId);
        flightMealService.deleteFlightMeal(flightMealId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<FlightMealResponse>> getFlightMealsByFlightId(@PathVariable Long flightId) {
        log.info("REST request to get flight meals by flight ID: {}", flightId);
        List<FlightMealResponse> responses = flightMealService.getFlightMealsByFlightId(flightId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<FlightMealResponse>> getFlightMeals(@RequestParam List<Long> ids) {
        log.info("REST request to get flight meals by IDs: {}", ids);
        List<FlightMealResponse> responses = flightMealService.getFlightMeals(ids);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/price")
    public ResponseEntity<Double> calculateMealPrice(@RequestParam List<Long> mealIds) {
        log.info("REST request to calculate meal price for IDs: {}", mealIds);
        Double totalPrice = flightMealService.calculateMealPrice(mealIds);
        return ResponseEntity.ok(totalPrice);
    }
}

