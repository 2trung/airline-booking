package com.airline.dto.response;

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
public class FlightMealResponse {
    Long id;
    Long flightId;
    MealResponse meal;
    Boolean available;
    Double price;
    String currency;
    Integer maxQuantity;
    String serviceClassRestriction;
    Integer displayOrder;
    Boolean complimentary;
    String notes;
}
