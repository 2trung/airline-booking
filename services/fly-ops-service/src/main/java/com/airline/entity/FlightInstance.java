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
    private Long id;

    private Long airlineId;

    @ManyToOne
    private Flight flight;

    @Column(nullable = false)
    private Long departureAirportId;
    @Column(nullable = false)
    private Long arrivalAirportId;
    @Column(nullable = false)
    private Long scheduleId;

    private Instant departureTime;

    private Instant arrivalTime;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    private Integer minAdvanceBookingDays;
    private Integer maxAdvanceBookingDays;

    private Boolean isActive = true;

    public String getFormattedDuration() {
        if (departureTime != null && arrivalTime != null) {
            long durationInMinutes = java.time.Duration.between(departureTime, arrivalTime).toMinutes();
            long hours = durationInMinutes / 60;
            long minutes = durationInMinutes % 60;
            return String.format("%02dh %02dmin", hours, minutes);
        }
        return "N/A";
    }

}
