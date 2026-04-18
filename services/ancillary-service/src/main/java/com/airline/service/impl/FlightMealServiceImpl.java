package com.airline.service.impl;

import com.airline.dto.request.FlightMealRequest;
import com.airline.dto.response.FlightMealResponse;
import com.airline.entity.FlightMeal;
import com.airline.entity.Meal;
import com.airline.mapper.FlightMealMapper;
import com.airline.repository.FlightMealRepository;
import com.airline.repository.MealRepository;
import com.airline.service.FlightMealService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class FlightMealServiceImpl implements FlightMealService {
    private final MealRepository mealRepository;
    private final FlightMealRepository flightMealRepository;

    @Override
    public FlightMealResponse createFlightMeal(FlightMealRequest flightMealRequest) {
        Meal meal = mealRepository.findById(flightMealRequest.getMealId())
                .orElseThrow(() -> new RuntimeException("Meal not found with id: " + flightMealRequest.getMealId()));
        if (flightMealRepository.existsByFlightIdAndMealId(flightMealRequest.getFlightId(), flightMealRequest.getMealId())) {
            throw new RuntimeException("Meal already exists for this flight");
        }
        FlightMeal flightMeal = FlightMeal
                .builder()
                .flightId(flightMealRequest.getFlightId())
                .meal(meal)
                .available(flightMealRequest.getAvailable() != null ? flightMealRequest.getAvailable() : true)
                .price(flightMealRequest.getPrice())
                .displayOrder(flightMealRequest.getDisplayOrder())
                .build();

        FlightMeal savedFlightMeal = flightMealRepository.save(flightMeal);
        return FlightMealMapper.toResponse(savedFlightMeal);
    }

    @Override
    public FlightMealResponse getFlightMealById(Long id) {

        FlightMeal flightMeal = flightMealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlightMeal not found with id: " + id));
        return FlightMealMapper.toResponse(flightMeal);
    }

    @Override
    public void deleteFlightMeal(Long id) {
        FlightMeal flightMeal = flightMealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlightMeal not found with id: " + id));
        flightMealRepository.delete(flightMeal);
    }

    @Override
    public FlightMealResponse updateFlightMeal(Long id, FlightMealRequest flightMealRequest) {
        FlightMeal flightMeal = flightMealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FlightMeal not found with id: " + id));

        flightMeal.setFlightId(flightMealRequest.getFlightId() != null ? flightMealRequest.getFlightId() : flightMeal.getFlightId());
        if (flightMealRequest.getMealId() != null) {
            Meal meal = mealRepository.findById(flightMealRequest.getMealId())
                    .orElseThrow(() -> new RuntimeException("Meal not found with id: " + flightMealRequest.getMealId()));
            flightMeal.setMeal(meal);
        }
        if (flightMealRequest.getAvailable() != null) {
            flightMeal.setAvailable(flightMealRequest.getAvailable());
        }
        if (flightMealRequest.getPrice() != null) {
            flightMeal.setPrice(flightMealRequest.getPrice());
        }
        if (flightMealRequest.getDisplayOrder() != null) {
            flightMeal.setDisplayOrder(flightMealRequest.getDisplayOrder());
        }

        FlightMeal updatedFlightMeal = flightMealRepository.save(flightMeal);
        return FlightMealMapper.toResponse(updatedFlightMeal);
    }

    @Override
    public List<FlightMealResponse> getFlightMealsByFlightId(Long flightId) {
        return flightMealRepository.findByFlightId(flightId).stream()
                .map(FlightMealMapper::toResponse)
                .toList();
    }

    @Override
    public List<FlightMealResponse> getFlightMeals(List<Long> ids) {
        return flightMealRepository.findAllById(ids).stream()
                .map(FlightMealMapper::toResponse)
                .toList();
    }

    @Override
    public Double calculateMealPrice(List<Long> mealIds) {
        List<FlightMeal> flightMeals = flightMealRepository.findAllById(mealIds);
        return flightMeals.stream()
                .map(FlightMeal::getPrice)
                .reduce(0.0, Double::sum);
    }
}
