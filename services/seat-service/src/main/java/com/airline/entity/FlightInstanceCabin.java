package com.airline.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class FlightInstanceCabin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    Long flightInstanceId;

    @ManyToOne
    CabinClass cabinClass;

    @Column(nullable = false)
    Integer totalSeats;

    Integer bookedSeats = 0;

    //todo: do it later
//    List<SeatInstance> seatInstances = new ArrayList<>();

    public Integer getAvailableSeats() {
        return totalSeats - bookedSeats;
    }
}
