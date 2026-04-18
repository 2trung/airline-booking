package com.airline.mapper;

import com.airline.dto.request.MealRequest;
import com.airline.dto.response.MealResponse;
import com.airline.entity.Meal;

public class MealMapper {

    public static Meal toEntity(MealRequest request) {
        if (request == null) {
            return null;
        }

        return Meal.builder()
                .airlineId(request.getAirlineId())
                .code(request.getCode())
                .name(request.getName())
                .mealType(request.getMealType())
                .dietaryRestrictions(request.getDietaryRestrictions())
                .ingredients(request.getIngredients())
                .imageUrl(request.getImageUrl())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .requireAdvanceBooking(request.getRequireAdvanceBooking() != null ? request.getRequireAdvanceBooking() : false)
                .advanceBookingHours(request.getAdvanceBookingHours())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
    }

    public static MealResponse toResponse(Meal meal) {
        if (meal == null) {
            return null;
        }

        return MealResponse.builder()
                .id(meal.getId())
                .airlineId(meal.getAirlineId())
                .code(meal.getCode())
                .name(meal.getName())
                .mealType(meal.getMealType())
                .dietaryRestrictions(meal.getDietaryRestrictions())
                .ingredients(meal.getIngredients())
                .imageUrl(meal.getImageUrl())
                .available(meal.getAvailable())
                .requireAdvanceBooking(meal.getRequireAdvanceBooking())
                .advanceBookingHours(meal.getAdvanceBookingHours())
                .displayOrder(meal.getDisplayOrder())
                .createdAt(meal.getCreatedAt())
                .updatedAt(meal.getUpdatedAt())
                .build();
    }

    public static void updateEntityFromRequest(Meal meal, MealRequest request) {
        if (meal == null || request == null) {
            return;
        }

        if (request.getCode() != null) {
            meal.setCode(request.getCode());
        }
        if (request.getName() != null) {
            meal.setName(request.getName());
        }
        if (request.getMealType() != null) {
            meal.setMealType(request.getMealType());
        }
        if (request.getDietaryRestrictions() != null) {
            meal.setDietaryRestrictions(request.getDietaryRestrictions());
        }
        if (request.getIngredients() != null) {
            meal.setIngredients(request.getIngredients());
        }
        if (request.getImageUrl() != null) {
            meal.setImageUrl(request.getImageUrl());
        }
        if (request.getAvailable() != null) {
            meal.setAvailable(request.getAvailable());
        }
        if (request.getRequireAdvanceBooking() != null) {
            meal.setRequireAdvanceBooking(request.getRequireAdvanceBooking());
        }
        if (request.getAdvanceBookingHours() != null) {
            meal.setAdvanceBookingHours(request.getAdvanceBookingHours());
        }
        if (request.getDisplayOrder() != null) {
            meal.setDisplayOrder(request.getDisplayOrder());
        }

    }
}

