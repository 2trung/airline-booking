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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SeatMapRequest {

    @NotBlank(message = "Seat map name is required")
    String name;

    @NotNull(message = "Total rows is required")
    @Positive(message = "Total rows must be a positive number")
    Integer totalRows;

    @NotNull(message = "Left seats per row is required")
    @Positive
    Integer leftSeatsPerRow;

    @NotNull(message = "Right seats per row is required")
    @Positive(message = "Right seats per row must be a positive number")
    Integer rightSeatsPerRow;

    Long cabinClassId;
}
