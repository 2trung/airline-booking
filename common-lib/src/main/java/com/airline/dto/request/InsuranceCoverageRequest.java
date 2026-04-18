package com.airline.dto.request;

import com.airline.enums.CoverageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class InsuranceCoverageRequest {

    @NotNull(message = "Ancillary ID is required")
    Long ancillaryId;

    @NotNull(message = "Coverage type is required")
    CoverageType coverageType;

    @NotBlank(message = "Coverage name is required")
    @Size(max = 200, message = "Coverage name must not exceed 200 characters")
    String name;

    @Size(max = 1000, message = "Coverage description must not exceed 1000 characters")
    String description;

    @NotNull(message = "Coverage amount is required")
    @PositiveOrZero(message = "Coverage amount must be zero or positive")
    Double coverageAmount;

    Boolean isFlat;

    @Size(max = 1000, message = "Claim condition must not exceed 1000 characters")
    String claimCondition;

    @Size(max = 100, message = "Emergency contact must not exceed 100 characters")
    String emergencyContact;

    Integer displayOrder;

    Boolean active;
}
