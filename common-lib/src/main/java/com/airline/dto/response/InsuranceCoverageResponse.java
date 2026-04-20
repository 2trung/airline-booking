package com.airline.dto.response;

import com.airline.enums.CoverageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InsuranceCoverageResponse {
    Long id;
    Long ancillaryId;
    String ancillaryName;
    CoverageType coverageType;
    String name;
    String description;
    Double coverageAmount;
    String currency;
    Boolean isFlat;
    String claimCondition;
    String emergencyContact;
    Integer displayOrder;
    Boolean active;
}
