package com.airline.dto.request;

import com.airline.enums.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class SeatRequest {

    @NotBlank(message = "Seat number is required")
    @Size(min = 2, max = 10)
    String seatNumber;

    @NotNull(message = "Seat row is required")
    @Positive
    Integer seatRow;

    Character columnLetter;

    @NotNull(message = "Seat type is required")
    SeatType seatType;

    @NotNull(message = "Seat map ID is required")
    Long seatMapId;

    Long cabinClassId;

    Boolean isAvailable;
    Boolean isBlocked;
    Boolean isEmergencyExit;
    Boolean isActive;

    Double basePrice;
    Double premiumSurcharge;

    Boolean hasExtraLegroom;
    Boolean hasBassinet;
    Boolean isNearLavatory;
    Boolean isNearGalley;
    Boolean hasPowerOutlet;
    Boolean hasTvScreen;
    Boolean isWheelchairAccessible;
    Boolean hasExtraWidth;

    Integer seatPitch;
    Integer seatWidth;
    Integer reclineAngle;
}
