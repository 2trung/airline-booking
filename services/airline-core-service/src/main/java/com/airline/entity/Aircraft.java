package com.airline.entity;

import com.airline.enums.AircraftStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    Long id;

    @Column(nullable = false, unique = true, length = 20)
    String code;

    @Column(nullable = false, length = 50)
    String model;

    @Column(nullable = false, length = 50)
    String manufacturer;

    @Column(nullable = false)
    Integer seatingCapacity;

    @Column
    Integer economySeats;

    @Column
    Integer premiumEconomySeats;

    @Column
    Integer businessSeats;

    @Column
    Integer firstClassSeats;

    @Column
    Integer rangeKm;

    @Column
    Integer cruisingSpeedKmh;

    @Column
    Integer maxAltitudeFt;

    @Column
    Integer yearOfManufacture;

    @Column
    LocalDate registrationDate;

    @Column
    LocalDate nextMaintenanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    AircraftStatus status = AircraftStatus.ACTIVE;

    @Column(nullable = false)
    Boolean isAvailable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Airline airline;

    @Column(name = "current_airport_id")
    Long currentAirportId;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    public Integer getTotalSeats() {
        return (economySeats != null ? economySeats : 0) + (premiumEconomySeats != null ? premiumEconomySeats : 0) + (businessSeats != null ? businessSeats : 0) + (firstClassSeats != null ? firstClassSeats : 0);
    }

    public boolean isOperational() {
        return AircraftStatus.ACTIVE.equals(status) && Boolean.TRUE.equals(isAvailable);
    }

    public boolean requiresMaintenance() {
        return nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDate.now().plusWeeks(2));
    }
}
