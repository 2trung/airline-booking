package com.airline.entity;

import com.airline.enums.AircraftStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private Integer capacity;
    @Column
    private Integer economySeats = 0;
    @Column
    private Integer premiumEconomySeats = 0;
    @Column
    private Integer businessSeats = 0;
    @Column
    private Integer firstClassSeats = 0;
    @Column
    private Integer cruiseSpeed;
    @Column
    private Integer yearOfManufacture;

    @Column
    private Integer rangeInKm;

    @Column
    private LocalDate registrationDate;

    @Column
    private LocalDate nextMaintenanceDate;

    @Column
    private Integer maxAltitudeInFeet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AircraftStatus status = AircraftStatus.ACTIVE;

    @ManyToOne()
    private Airline airline;

    private Long currentAirportId;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Integer getTotalSeats() {
        return economySeats + premiumEconomySeats + businessSeats + firstClassSeats;
    }

    public boolean isOperational() {
        return status == AircraftStatus.ACTIVE;
    }

    public boolean requiresMaintenance() {
        return nextMaintenanceDate != null && nextMaintenanceDate.isBefore(LocalDate.now().plusWeeks(2));
    }

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = AircraftStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
