package com.airline.integration.impl;

import com.airline.client.PricingClient;
import com.airline.dto.response.FareResponse;
import com.airline.integration.PricingIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PriceIntegrationServiceImpl implements PricingIntegrationService {

    private final PricingClient pricingClient;

    @Override
    public Double calculateFareTotal(Long fareId) {
        FareResponse fare = pricingClient.getFareById(fareId);
        Double baseFare = fare.getBaseFare();
        Double taxesAndFees = fare.getTaxesAndFees() != null ? fare.getTaxesAndFees() : 0.0;
        Double airlineFees = fare.getAirlineFees() != null ? fare.getAirlineFees() : 0.0;
        return baseFare + taxesAndFees + airlineFees;

    }
}
