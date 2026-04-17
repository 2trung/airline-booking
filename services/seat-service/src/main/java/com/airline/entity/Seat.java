package com.airline.entity;

import com.airline.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;


    String seatNumber;

    Integer seatRow;

    Character columnLetter;

    SeatType seatType;

    Double basePrice;

    Double premiumSuperCharge;

    Boolean isAvailable = true;

    Boolean isActive = true;

    Boolean isBlocked = false;

    Boolean isEmergencyExit = false;

    Boolean hasExtraLegRoom = false;

    Boolean hasPowerOutlet = false;

    Boolean hasTvScreen = false;

    Boolean hasExtraWidth = false;

    Integer seatPitch;
    Integer seatWidth;
    Integer reclineAngle;

    @ManyToOne
    SeatMap seatMap;

    @ManyToOne
    CabinClass cabinClass;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @CreatedBy
    String createdBy;

    @LastModifiedBy
    String updatedBy;

    @Version
    Long version;

    public Double getTotalPrice() {
        return basePrice + premiumSuperCharge;
    }

    public boolean isBookable() {
        return isAvailable && isActive && !isBlocked;
    }

    public String getFullPosition() {
        return seatRow + "" + columnLetter;
    }

}
