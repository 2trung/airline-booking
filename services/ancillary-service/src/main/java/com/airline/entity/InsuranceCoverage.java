package com.airline.entity;

import com.airline.enums.CoverageType;
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
public class InsuranceCoverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    Ancillary ancillary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CoverageType coverageType;

    @Column(nullable = false, length = 200)
    String name;

    @Column(length = 1000)
    String description;

    @Column(nullable = false)
    Double coverageAmount;

    @Column(length = 3)
    @Builder.Default
    String currency = "USD";

    @Builder.Default
    boolean isFlat = true;

    @Column(length = 500)
    String claimCondition;

    @Column(length = 100)
    String emergencyContact;

    Integer displayOrder;

    @Builder.Default
    boolean active = true;

}
