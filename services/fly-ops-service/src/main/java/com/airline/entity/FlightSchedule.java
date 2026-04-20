package com.airline.entity;

import com.airline.enums.RecurrenceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlightSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    Flight flight;

    @Column(nullable = false)
    Long departureAirportId;

    @Column(nullable = false)
    Long arrivalAirportId;

    @Column(nullable = false)
    LocalTime departureTime;

    @Column(nullable = false)
    LocalTime arrivalTime;

    @Column(nullable = false)
    LocalDate startDate;

    @Column(nullable = false)
    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    RecurrenceType recurrenceType;

    @ElementCollection
    @CollectionTable(name = "schedule_operating_days", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    List<DayOfWeek> operatingDays;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;

    @Version
    Long version;
}
