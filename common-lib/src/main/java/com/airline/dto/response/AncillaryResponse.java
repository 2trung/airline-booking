package com.airline.dto.response;

import com.airline.domain.AncillaryMetadata;
import com.airline.enums.AncillaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AncillaryResponse {
    Long id;
    AncillaryType type;
    String subType;
    String rfisc;
    String name;
    String description;
    String categoryDisplayName;
    String categoryIcon;
    String iconUrl;
    AncillaryMetadata metadata;
    List<InsuranceCoverageResponse> coverages;
    Integer displayOrder;
    Long airlineId;
}
