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
public class FlightCabinAncillary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false)
    Long flightId;

    @Column(nullable = false)
    Long cabinClassId;

    @ManyToOne
    Ancillary ancillary;

    Boolean available;

    Integer maxQuantity;

    Double price;

    Boolean includedInFare = false;
}
