package com.airline.client;

import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.dto.response.FlightMealResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AncillaryClientFallback implements AncillaryClient {

    @Override
    public double calculateAncillariesPrice(List<Long> flightCabinAncillaryIds) {
        return 0.0;
    }

    @Override
    public List<FlightCabinAncillaryResponse> getAllByIds(List<Long> Ids) {
        return Collections.emptyList();
    }

    @Override
    public List<FlightMealResponse> getMealsByIds(List<Long> Ids) {
        return Collections.emptyList();
    }

    @Override
    public Double calculateMealPrice(List<Long> requests) {
        return 0.0;
    }
}
