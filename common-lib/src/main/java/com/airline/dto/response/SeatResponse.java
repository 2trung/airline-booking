package com.airline.dto.response;

import com.airline.enums.SeatType;
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
public class SeatResponse {
    Long id;
    String seatNumber;
    Integer seatRow;
    Character columnLetter;
    SeatType seatType;

    Boolean isAvailable;
    Boolean isBlocked;
    Boolean isEmergencyExit;
    Boolean isActive;

    Double basePrice;
    Double premiumSurcharge;
    Double totalPrice;

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

    Long seatMapId;
    String seatMapName;
    Long cabinClassId;
    String cabinClassName;

    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    Boolean isPremiumSeat;
    Boolean isBookable;
    String fullPosition;
    String seatCharacteristics;
}
