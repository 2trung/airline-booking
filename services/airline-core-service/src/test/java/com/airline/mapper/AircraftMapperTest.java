package com.airline.mapper;

import com.airline.dto.request.AircraftRequest;
import com.airline.entity.Aircraft;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AircraftMapperTest {

    @Test
    void toEntityShouldMapSeatingAndNormalizeCode() {
        AircraftRequest request = AircraftRequest.builder()
                .code(" ab-123 ")
                .model("A320")
                .manufacturer("Airbus")
                .seatingCapacity(180)
                .economySeats(140)
                .businessSeats(40)
                .build();

        Aircraft aircraft = AircraftMapper.toEntity(request);

        assertNotNull(aircraft);
        assertEquals("AB-123", aircraft.getCode());
        assertEquals(180, aircraft.getCapacity());
        assertEquals(140, aircraft.getEconomySeats());
        assertEquals(0, aircraft.getPremiumEconomySeats());
        assertEquals(40, aircraft.getBusinessSeats());
        assertEquals(0, aircraft.getFirstClassSeats());
    }
}

