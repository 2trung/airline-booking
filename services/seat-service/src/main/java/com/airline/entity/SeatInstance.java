package com.airline.entity;

import com.airline.enums.SeatAvailabilityStatus;
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
public class SeatInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long flightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    FlightInstanceCabin flightInstanceCabin;

    @Column()
    Long flightInstanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SeatAvailabilityStatus status = SeatAvailabilityStatus.AVAILABLE;

    boolean isBooked = false;
    boolean isAvailable = true;

    String mealPreference;
    Double fare;
    Double premiumSurcharge;

    @Version
    Long version;

    @Column()
    Long flightScheduleId;

    @CreatedDate
    @Column(updatable = false)
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
