package com.airline.converter;

import com.airline.domain.AncillaryMetadata;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import tools.jackson.databind.ObjectMapper;

@Converter
public class AncillaryMetadataConverter implements AttributeConverter<AncillaryMetadata, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(AncillaryMetadata ancillaryMetadata) {
        if (ancillaryMetadata == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(ancillaryMetadata);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting AncillaryMetadata to JSON", e);
        }
    }

    @Override
    public AncillaryMetadata convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, AncillaryMetadata.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to AncillaryMetadata", e);
        }
    }
}
