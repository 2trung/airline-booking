package com.airline.entity;

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
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 10)
    String code;

    @Column(nullable = false, length = 200)
    String name;

    @Column(nullable = false, length = 50)
    String mealType;

    @Column(length = 100)
    String dietaryRestriction;

    @Column(length = 2000)
    String ingredients;

    @Column(length = 500)
    String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    Boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    Boolean requiresAdvanceBooking = false;

    Integer advanceBookingHours;

    @Builder.Default
    Integer displayOrder = 0;

    @Column(nullable = false)
    Long airlineId;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    Instant updatedAt;
}
