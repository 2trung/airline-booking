package com.airline.dto.request;

import com.airline.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
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
public class SeatRequest {

    @NotBlank(message = "Seat number is required")
    String seatNumber;

    @NotNull(message = "Seat row is required")
    Integer seatRow;
    Character columnLetter;

    @NotNull(message = "Seat type is required")
    SeatType seatType;

    @NotNull(message = "Seat map ID is required")
    Long seatMapId;

    Long cabinClassId;

    Boolean isAvailable;
    Boolean isActive;
    Boolean isBlocked;
    Boolean isEmergencyExit;

    Boolean hasExtraLegRoom = false;
    Boolean hasPowerOutlet = false;
    Boolean hasTvScreen = false;
    Boolean hasExtraWidth = false;

    Integer seatPitch;
    Integer seatWidth;
    Integer reclineAngle;
}
