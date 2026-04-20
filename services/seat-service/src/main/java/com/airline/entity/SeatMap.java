package com.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SeatMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    int totalRows;

    @Column(nullable = false)
    int rightSeatsPerRow;

    @Column(nullable = false)
    int leftSeatsPerRow;

    @Column(nullable = false)
    Long airlineId;

    @OneToMany(mappedBy = "seatMap", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Seat> seats;

    @OneToOne
    @JoinColumn()
    CabinClass cabinClass;
}
