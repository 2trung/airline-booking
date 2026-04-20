package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CabinClassRequest {

    @NotBlank(message = "Name is required")
    String name;

    @NotBlank
    @Size(min = 1, max = 5)
    String code;

    @Size(max = 500)
    String description;

    @NotNull
    Long aircraftId;

    Integer displayOrder;
    Boolean isActive;
    Boolean isBookable;
    Integer typicalSeatPitch;
    Integer typicalSeatWidth;
    String seatType;
}
