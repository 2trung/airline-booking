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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AncillaryRequest {

    @NotNull(message = "Ancillary type is required")
    AncillaryType type;

    @Size(max = 100, message = "Sub-type cannot be longer than 100 characters")
    String subType;

    @Size(max = 10, message = "RFISC code cannot be longer than 10 characters")
    String rfisc;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name cannot be longer than 200 characters")
    String name;

    @Size(max = 1000, message = "Description cannot be longer than 1000 characters")
    String description;

    @Size(max = 500, message = "Icon URL cannot be longer than 500 characters")
    String iconUrl;

    AncillaryMetadata metadata;

    Integer displayOrder;
}
