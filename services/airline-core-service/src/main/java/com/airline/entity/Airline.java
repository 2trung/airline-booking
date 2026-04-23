package com.airline.entity;

import com.airline.embeddable.Support;
import com.airline.enums.AirlineStatus;
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
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    Long id;

    @Column(length = 2, nullable = false, unique = true)
    String iataCode;

    @Column(length = 3, nullable = false, unique = true)
    String icaoCode;

    @Column(nullable = false)
    String name;

    String alias;

    @Column(nullable = false)
    String country;

    String logoUrl;

    String website;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    AirlineStatus status = AirlineStatus.ACTIVE;

    String alliance;

    @Embedded
    Support support;

    @Column(name = "headquarters_city_id")
    Long headquartersCityId;

    @Column(name = "owner_id", updatable = false, nullable = false)
    Long ownerId;

    @Column
    Long updatedById;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
}
