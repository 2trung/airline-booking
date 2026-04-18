package com.airline.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class FlightMealRequest {

    @NotNull(message = "Flight id is required")
    Long flightId;


    @NotNull(message = "Meal id is required")
    Long mealId;

    Boolean available;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or positive")
    Double price;

    @Min(value = 0, message = "Display order must be zero or positive")
    Integer displayOrder;
}
