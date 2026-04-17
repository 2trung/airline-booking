package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SeatMapRequest {
    @NotBlank(message = "Seat map name is required")
    String name;

    @Positive(message = "Total columns must be a positive number")
    @NotNull(message = "Total columns is required")
    Integer totalRows;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be a positive number")
    Integer leftSeatsPerRow;

    @NotNull(message = "Total seats is required")
    @Positive(message = "Total seats must be a positive number")
    Integer rightSeatsPerRow;

    Long cabinClassId;

    @NotNull(message = "Airline ID is required")
    Long airlineId;
}
