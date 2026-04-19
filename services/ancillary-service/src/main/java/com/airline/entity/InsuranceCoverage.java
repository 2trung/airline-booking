package com.airline.entity;

import com.airline.enums.CoverageType;
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
public class InsuranceCoverage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ManyToOne
    Ancillary ancillary;

    @Column(nullable = false)
    CoverageType coverageType;

    @Column(nullable = false)
    String name;

    @Column(length = 1000)
    String description;

    @Column(nullable = false)
    Double coverageAmount;


    Boolean isFlat = false;
    String claimCondition;
    Integer displayOrder;
    String emergencyContact;
    Boolean active;

}
