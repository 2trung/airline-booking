package com.airline.entity;

import com.airline.enums.SeatAvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;
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
public class SeatInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    Long flightId;

    @ManyToOne
    FlightInstanceCabin flightInstanceCabin;

    private Long flightInstanceId;

    @ManyToOne
    Seat seat;

    SeatAvailabilityStatus status = SeatAvailabilityStatus.AVAILABLE;

    Boolean isBooked = false;
    Boolean isAvailable = true;

    Double fare;
    Double premiumSuperCharge;

    @Version
    Long version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
