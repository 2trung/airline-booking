package com.airline.entity;

import com.airline.embeddable.Support;
import com.airline.enums.AirlineStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 2)
    private String iataCode;

    @Column(unique = true, nullable = false, length = 3)
    private String icaoCode;

    @Column(nullable = false)
    private String name;

    private String alias;

    private String logoUrl;

    private String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AirlineStatus status = AirlineStatus.ACTIVE;

    private String alliance;

    private Long headquartersCityId;

    @Column(nullable = false)
    private Long ownerId;

    private Long updatedById;

    @Embedded
    private Support support;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) {
            status = AirlineStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }
}
