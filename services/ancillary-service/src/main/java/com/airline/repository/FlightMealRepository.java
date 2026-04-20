package com.airline.repository;

import com.airline.entity.FlightMeal;
import com.airline.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FlightMealRepository extends JpaRepository<FlightMeal, Long> {
    Optional<FlightMeal> findByFlightIdAndMeal(Long flightId, Meal meal);

    void deleteByFlightId(Long flightId);

    List<FlightMeal> findByFlightId(Long flightId);

    boolean existsByFlightIdAndMeal_Id(Long flightId, Long mealId);
}
