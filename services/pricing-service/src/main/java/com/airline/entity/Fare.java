package com.airline.entity;

import com.airline.embeddable.*;
import com.airline.enums.CabinClassType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
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
public class Fare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    Character rbdCode;

    @Column(nullable = false)
    Long flightId;

    @Column(nullable = false)
    Long cabinClassId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    CabinClassType cabinClass;

    @Column(nullable = false)
    Double baseFare;

    Double taxesAndFees;
    Double airlineFees;

    @Column(nullable = false)
    Double currentPrice;

    @Column(length = 100)
    String fareLabel;

    @OneToOne(mappedBy = "fare", cascade = CascadeType.ALL, orphanRemoval = true)
    BaggagePolicy baggagePolicy;

    @OneToOne(mappedBy = "fare", cascade = CascadeType.ALL, orphanRemoval = true)
    FareRules fareRules;

    @Embedded
    @Builder.Default
    SeatBenefits seatBenefits = new SeatBenefits();

    @Embedded
    @Builder.Default
    BoardingBenefits boardingBenefits = new BoardingBenefits();

    @Embedded
    @Builder.Default
    InFlightBenefits inFlightBenefits = new InFlightBenefits();

    @Embedded
    @Builder.Default
    FlexibilityBenefits flexibilityBenefits = new FlexibilityBenefits();

    @Embedded
    @Builder.Default
    PremiumServiceBenefits premiumServiceBenefits = new PremiumServiceBenefits();

    @Column(updatable = false, nullable = false)
    @CreatedDate
    Instant createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    Instant updatedAt;

    public Double getTotalPrice() {
        return baseFare
                + (airlineFees != null ? airlineFees : 0.0)
                + (taxesAndFees != null ? taxesAndFees : 0.0);
    }
}
