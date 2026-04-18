package com.airline.dto.request;

import com.airline.domain.AncillaryMetadata;
import com.airline.enums.AncillaryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AncillaryRequest {
    @NotNull(message = "Ancillary type is required")
    AncillaryType type;

    @Size(max = 100, message = "Sub-type must not exceed 100 characters")
    String subType;

    @Size(max = 10, message = "RFISC must not exceed 10 characters")
    String rfisc;

    @NotBlank(message = "Ancillary name is required")
    @Size(max = 200, message = "Ancillary name must not exceed 200 characters")
    String name;

    @Size(max = 1000, message = "Ancillary description must not exceed 1000 characters")
    String description;

    @Size(max = 500, message = "Ancillary icon URL must not exceed 500 characters")
    String iconUrl;

    AncillaryMetadata metadata;

    Integer displayOrder;
}
