package com.airline.entity;

import com.airline.enums.FlightStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FlightInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long airlineId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    Flight flight;

    @Column(nullable = false)
    Long departureAirportId;

    @Column(nullable = false)
    Long arrivalAirportId;

    @Column(nullable = false)
    Long scheduleId;

    @Column(nullable = false)
    Instant departureDateTime;

    @Column(nullable = false)
    Instant arrivalDateTime;

    @Column(nullable = false)
    Integer totalSeats;

    @Column(nullable = false)
    Integer availableSeats;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    FlightStatus status;

    Integer minAdvanceBookingDays;
    Integer maxAdvanceBookingDays;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;

    String terminal;
    String gate;

    @Version
    Long version;

    @Transient
    public String getFormattedDuration() {
        if (departureDateTime != null && arrivalDateTime != null) {
            long durationInMinutes = java.time.Duration.between(departureDateTime, arrivalDateTime).toMinutes();
            long hours = durationInMinutes / 60;
            long minutes = durationInMinutes % 60;
            return String.format("%02dh %02dmin", hours, minutes);
        }
        return "N/A";
    }

}
