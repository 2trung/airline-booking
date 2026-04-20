package com.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlightMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long flightId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Meal meal;

    @Column(nullable = false)
    @Builder.Default
    Boolean available = true;

    Double price;

    @Builder.Default
    Integer displayOrder = 0;
}
