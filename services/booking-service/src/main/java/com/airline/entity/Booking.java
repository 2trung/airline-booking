package com.airline.entity;

import com.airline.embeddable.ContactInfo;
import com.airline.enums.BookingStatus;
import com.airline.enums.CabinClassType;
import com.airline.enums.TripType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false, unique = true)
    String bookingReference;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    Long flightId;

    @Column(nullable = false)
    Long flightInstanceId;

    @Column(nullable = false)
    Long airlineId;

    @Enumerated(EnumType.STRING)
    CabinClassType cabinClass;

    @Enumerated(EnumType.STRING)
    TripType tripType;

    @Column(nullable = false)
    Long fareId;

    Boolean flexibleTicket;

    Instant ticketTimeLimit;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Passenger> passengers = new HashSet<>();

    @ElementCollection
    List<Long> seatInstanceIds;

    @ElementCollection
    List<Long> ancillaryIds;

    @ElementCollection
    List<Long> mealIds;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Ticket> tickets = new HashSet<>();

    Long paymentId;

    @Enumerated(EnumType.STRING)
    BookingStatus status;

    @CreatedDate
    Instant bookingDate;

    @LastModifiedDate
    Instant lastModified;

    Boolean ticketIssued;

    @Embedded
    ContactInfo contactInfo;
}
