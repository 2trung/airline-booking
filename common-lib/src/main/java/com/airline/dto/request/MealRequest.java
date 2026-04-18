package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MealRequest {
    Long airlineId;

    @NotBlank(message = "Meal code is required")
    String code;

    @NotBlank(message = "Meal name is required")
    String name;

    @NotBlank(message = "Meal type is required")
    @Size(max = 50, message = "Meal type must not exceed 50 characters")
    String mealType;

    @Size(max = 100, message = "Meal description must not exceed 100 characters")
    String dietaryRestrictions;

    @Size(max = 2000, message = "Meal ingredients must not exceed 2000 characters")
    String ingredients;

    @Size(max = 500, message = "Meal image URL must not exceed 500 characters")
    String imageUrl;

    Boolean available;
    Boolean requireAdvanceBooking;
    Integer advanceBookingHours;
    Integer displayOrder;
}
