package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MealResponse {
    Long id;
    String code;
    String name;
    String description;
    String mealType;
    String dietaryRestriction;
    String ingredients;
    String allergens;
    String nutritionalInfo;
    String imageUrl;
    Double price;
    String currency;
    Boolean available;
    Boolean requiresAdvanceBooking;
    Integer advanceBookingHours;
    Integer displayOrder;
    Long airlineId;
    Instant createdAt;
    Instant updatedAt;
}
