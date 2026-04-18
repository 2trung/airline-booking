package com.airline.service.impl;

import com.airline.dto.request.MealRequest;
import com.airline.dto.response.MealResponse;
import com.airline.entity.Meal;
import com.airline.mapper.MealMapper;
import com.airline.repository.MealRepository;
import com.airline.service.MealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealServiceImpl implements MealService {

    private final MealRepository mealRepository;

    @Override
    public MealResponse createMeal(Long airlineId, MealRequest request) {
        if (airlineId == null) {
            throw new RuntimeException("Airline ID is required");
        }

        Meal meal = MealMapper.toEntity(request);
        Meal savedMeal = mealRepository.save(meal);
        return MealMapper.toResponse(savedMeal);
    }

    @Override
    public MealResponse getMealById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        return MealMapper.toResponse(meal);
    }

    @Override
    public void deleteMeal(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));
        mealRepository.delete(meal);
    }

    @Override
    public MealResponse updateMeal(Long airlineId, Long id, MealRequest request) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found"));

        if (request.getCode() != null && mealRepository.existsByCodeAndAirlineIdAndIdNot(request.getCode(), airlineId, id)) {
            throw new RuntimeException("Meal code already exists for this airline");
        }

        MealMapper.updateEntityFromRequest(meal, request);
        Meal updatedMeal = mealRepository.save(meal);
        return MealMapper.toResponse(updatedMeal);
    }

    @Override
    public List<MealResponse> getMealsByAirlineId(Long airlineId) {
        return mealRepository.findByAirlineId(airlineId)
                .stream()
                .map(MealMapper::toResponse)
                .toList();
    }
}
