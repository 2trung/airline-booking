package com.airline.dto.response;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MealResponse {
    Long id;
    String code;
    String name;
    String description;
    String mealType;
    String dietaryRestrictions;
    String ingredients;
    String imageUrl;
    Boolean available = true;
    Boolean requireAdvanceBooking = false;
    Integer advanceBookingHours;
    Integer displayOrder = 0;
    Long airlineId;
    Instant createdAt;
    Instant updatedAt;
}
