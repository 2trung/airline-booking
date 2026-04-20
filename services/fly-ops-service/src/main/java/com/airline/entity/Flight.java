package com.airline.entity;

import com.airline.enums.FlightStatus;
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
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 10)
    String flightNumber;

    @Column(nullable = false)
    Long airlineId;

    @Column(nullable = false)
    Long aircraftId;

    @Column(nullable = false)
    Long departureAirportId;

    @Column(nullable = false)
    Long arrivalAirportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    FlightStatus status;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    Instant updatedAt;
}
