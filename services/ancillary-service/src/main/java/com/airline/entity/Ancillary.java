package com.airline.entity;

import com.airline.domain.AncillaryMetadata;
import com.airline.enums.AncillaryType;
import com.airline.service.AncillaryMetadataConverter;
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
public class Ancillary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Enumerated(EnumType.STRING)
    AncillaryType type;

    String subType;

    String rfisc;

    @Column(nullable = false)
    String name;

    String description;

    @Convert(converter = AncillaryMetadataConverter.class)
    AncillaryMetadata metadata;

    Integer displayOrder;

    Long airlineId;
}
