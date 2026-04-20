package com.airline.entity;

import com.airline.enums.SeatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Size(min = 2, max = 10)
    @Column(nullable = false, length = 10)
    String seatNumber;

    @Positive
    @Column(nullable = false)
    Integer seatRow;

    @Column()
    Character columnLetter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    SeatType seatType;

    @Column()
    Double basePrice;

    @Column()
    Double premiumSurcharge;

    @Builder.Default
    @Column(nullable = false)
    Boolean isAvailable = true;

    @Builder.Default
    @Column(nullable = false)
    Boolean isBlocked = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isEmergencyExit = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;

    @Builder.Default
    @Column(nullable = false)
    Boolean hasExtraLegroom = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean hasBassinet = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isNearLavatory = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isNearGalley = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean hasPowerOutlet = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean hasTvScreen = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean isWheelchairAccessible = false;

    @Builder.Default
    @Column(nullable = false)
    Boolean hasExtraWidth = false;

    Integer seatPitch;
    Integer seatWidth;
    Integer reclineAngle;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    SeatMap seatMap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    CabinClass cabinClass;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    String createdBy;

    @LastModifiedBy
    @Column()
    String updatedBy;

    @Version
    @Column()
    Long version;

    public Double getTotalPrice() {
        Double total = basePrice != null ? basePrice : 0.0;
        if (premiumSurcharge != null) {
            total = total + premiumSurcharge;
        }
        return total;
    }

    public boolean isBookable() {
        return isActive && isAvailable && !isBlocked;
    }

    public boolean isPremiumSeat() {

        return hasExtraLegroom || isEmergencyExit || hasExtraWidth;
    }

    public String getFullPosition() {
        return seatRow + "" + columnLetter;
    }

    @PrePersist
    @PreUpdate
    void validate() {
        if (seatNumber == null) {
            seatNumber = seatRow + "" + columnLetter;
        }
        if (isEmergencyExit && !hasExtraLegroom) {
            hasExtraLegroom = true;
        }
    }

    public String getSeatCharacteristics() {
        StringBuilder characteristics = new StringBuilder();
        if (hasExtraLegroom) characteristics.append("Extra Legroom, ");
        if (isEmergencyExit) characteristics.append("Emergency Exit, ");
        if (hasBassinet) characteristics.append("Bassinet, ");
        if (hasPowerOutlet) characteristics.append("Power, ");
        if (hasTvScreen) characteristics.append("TV, ");
        if (isWheelchairAccessible) characteristics.append("Wheelchair Access, ");
        if (characteristics.length() > 0) {
            characteristics.setLength(characteristics.length() - 2);
        }
        return characteristics.toString();
    }

}
