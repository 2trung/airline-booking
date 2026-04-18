package com.airline.entity;

import com.airline.enums.TicketStatus;
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
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    String ticketNumber;

    @Enumerated(EnumType.STRING)
    TicketStatus status;

    Instant issuedAt;

    @ManyToOne
    Booking booking;

    @ManyToOne
    Passenger passenger;

}
