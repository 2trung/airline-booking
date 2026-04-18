package com.airline.repository;

import com.airline.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByAirlineId(Long airlineId);

    boolean existsByCodeAndAirlineId(String code, Long airlineId);

    boolean existsByCodeAndAirlineIdAndIdNot(String code, Long airlineId, Long id);
}
