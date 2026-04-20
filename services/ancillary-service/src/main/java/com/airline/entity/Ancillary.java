package com.airline.entity;

import com.airline.domain.AncillaryMetadata;
import com.airline.enums.AncillaryType;
import com.airline.converter.AncillaryMetadataConverter;
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
public class Ancillary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    AncillaryType type;

    @Column(length = 100)
    String subType;

    @Column(length = 10)
    String rfisc;

    @Column(nullable = false, length = 200)
    String name;

    @Column(length = 1000)
    String description;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AncillaryMetadataConverter.class)
    AncillaryMetadata metadata;

    Integer displayOrder;

    @Column(nullable = false)
    Long airlineId;
}
