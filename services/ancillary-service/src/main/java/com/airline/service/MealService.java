package com.airline.service;

import com.airline.dto.request.MealRequest;
import com.airline.dto.response.MealResponse;

import java.util.List;

public interface MealService {
    MealResponse createMeal(Long airlineId, MealRequest request);

    MealResponse getMealById(Long id);

    void deleteMeal(Long id);

    MealResponse updateMeal(Long airlineId, Long id, MealRequest request);

    List<MealResponse> getMealsByAirlineId(Long airlineId);
}
