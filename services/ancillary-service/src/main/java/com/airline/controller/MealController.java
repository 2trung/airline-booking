package com.airline.controller;

import com.airline.dto.request.MealRequest;
import com.airline.dto.response.MealResponse;
import com.airline.service.MealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<MealResponse> createMeal(@Valid @RequestBody MealRequest request, @RequestHeader("X-Airline-Id") Long airlineId) {
        log.info("REST request to create meal: {}", request.getName());
        MealResponse response = mealService.createMeal(airlineId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{mealId}")
    public ResponseEntity<MealResponse> getMealById(@PathVariable Long mealId) {
        log.info("REST request to get meal by ID: {}", mealId);
        MealResponse response = mealService.getMealById(mealId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(@PathVariable Long mealId,
                                                   @Valid @RequestBody MealRequest request,
                                                   @RequestHeader("X-Airline-Id") Long airlineId) {
        log.info("REST request to update meal by ID: {}", mealId);
        MealResponse response = mealService.updateMeal(null, mealId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long mealId) {
        log.info("REST request to delete meal by ID: {}", mealId);
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<MealResponse>> getMealsByAirlineId(@PathVariable Long airlineId) {
        log.info("REST request to get meals by airline ID: {}", airlineId);
        List<MealResponse> responses = mealService.getMealsByAirlineId(airlineId);
        return ResponseEntity.ok(responses);
    }
}

