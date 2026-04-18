package com.airline.dto.response;

import com.airline.enums.SeatType;
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
public class SeatResponse {
    Long id;
    String seatNumber;
    Integer seatRow;
    Character columnLetter;
    SeatType seatType;

    Double basePrice;
    Double premiumSuperCharge;
    Double totalPrice;

    Boolean isAvailable;
    Boolean isActive ;
    Boolean isBlocked ;
    Boolean isEmergencyExit ;

    Boolean hasExtraLegRoom ;
    Boolean hasPowerOutlet ;
    Boolean hasTvScreen ;
    Boolean hasExtraWidth ;

    Integer seatPitch;
    Integer seatWidth;
    Integer reclineAngle;

    Long seatMapId;
    String seatMapName;
    Long carbinClassId;
    String cabinClassName;

    Instant createdAt;
    Instant updatedAt;

    Boolean isBookable;
    String fullPosition;

    String createdBy;
    String updatedBy;
}
