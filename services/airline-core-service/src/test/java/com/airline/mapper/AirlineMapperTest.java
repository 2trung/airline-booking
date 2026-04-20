package com.airline.mapper;

import com.airline.dto.request.AirlineRequest;
import com.airline.embeddable.Support;
import com.airline.entity.Airline;
import com.airline.enums.AirlineStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AirlineMapperTest {

    @Test
    void toEntityShouldNormalizeCodesAndMapSupport() {
        AirlineRequest request = AirlineRequest.builder()
                .iataCode(" aa ")
                .icaoCode(" aae ")
                .name("Air Alpha")
                .status(AirlineStatus.ACTIVE)
                .supportEmail("support@alpha.com")
                .supportPhone("+1000000000")
                .supportHours("24/7")
                .build();

        Airline airline = AirlineMapper.toEntity(request);

        assertNotNull(airline);
        assertEquals("AA", airline.getIataCode());
        assertEquals("AAE", airline.getIcaoCode());

        Support support = airline.getSupport();
        assertNotNull(support);
        assertEquals("support@alpha.com", support.getEmail());
        assertEquals("+1000000000", support.getPhone());
        assertEquals("24/7", support.getHours());
    }
}

