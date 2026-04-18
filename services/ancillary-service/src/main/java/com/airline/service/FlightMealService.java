package com.airline.service;

import com.airline.dto.request.FlightMealRequest;
import com.airline.dto.response.FlightMealResponse;

import java.util.List;

public interface FlightMealService {

    FlightMealResponse createFlightMeal(FlightMealRequest flightMealRequest);

    FlightMealResponse getFlightMealById(Long id);

    void deleteFlightMeal(Long id);

    FlightMealResponse updateFlightMeal(Long id, FlightMealRequest flightMealRequest);

    List<FlightMealResponse> getFlightMealsByFlightId(Long flightId);

    List<FlightMealResponse> getFlightMeals(List<Long> ids);

    Double calculateMealPrice(List<Long> mealIds);

}
