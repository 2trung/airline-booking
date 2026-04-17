package com.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SeatMap {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    String name;

    Integer totalRows;

    @Column(nullable = false)
    Integer rightSeatsPerRow;

    @Column(nullable = false)
    Integer leftSeatsPerRow;

    @Column(nullable = false)
    Long airlineId;

    @OneToOne
    CabinClass cabinClass;

    @OneToMany(mappedBy = "seatMap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats;
}
