package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MealRequest {

    @NotBlank(message = "Meal code is required")
    @Size(max = 10, message = "Meal code must not exceed 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Meal code must contain only uppercase letters and numbers")
    String code;

    @NotBlank(message = "Meal name is required")
    @Size(max = 200, message = "Meal name must not exceed 200 characters")
    String name;

    @NotBlank(message = "Meal type is required")
    @Size(max = 50, message = "Meal type must not exceed 50 characters")
    String mealType;

    @Size(max = 100, message = "Dietary restriction must not exceed 100 characters")
    String dietaryRestriction;

    @Size(max = 2000, message = "Ingredients list must not exceed 2000 characters")
    String ingredients;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    String imageUrl;

    @NotNull(message = "Availability status is required")
    Boolean available;

    Boolean requiresAdvanceBooking;

    Integer advanceBookingHours;

    Integer displayOrder;
}
