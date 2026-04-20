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
public class FlightCabinAncillary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long flightId;

    @Column(nullable = false)
    Long cabinClassId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Ancillary ancillary;

    @Column(nullable = false)
    @Builder.Default
    Boolean available = true;

    Integer maxQuantity;

    Double price;

    String currency;

    @Column(nullable = false)
    @Builder.Default
    Boolean includedInFare = false;
}
