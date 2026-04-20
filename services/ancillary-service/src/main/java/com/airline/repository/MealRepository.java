package com.airline.repository;

import com.airline.entity.Meal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {
    Optional<Meal> findByCode(String code);

    boolean exists(Specification<Meal> spec);

    List<Meal> findByAirlineId(Long airlineId);

    List<Meal> findByCodeAndAirlineId(String code, Long airlineId);

    boolean existsByCodeAndAirlineId(String code, Long airlineId);

}
