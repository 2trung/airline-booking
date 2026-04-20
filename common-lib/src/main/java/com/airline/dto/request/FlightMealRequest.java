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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightMealRequest {

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Meal ID is required")
    Long mealId;

    @NotNull(message = "Availability status is required")
    Boolean available;

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    Double price;

    @Min(value = 0, message = "Display order cannot be negative")
    Integer displayOrder;
}
