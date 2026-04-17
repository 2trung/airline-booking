package com.airline.service;

import com.airline.dto.request.FareRulesRequest;
import com.airline.dto.response.FareRulesResponse;

import java.util.List;

public interface FareRulesService {
    FareRulesResponse createFareRules(FareRulesRequest fareRulesRequest);

    FareRulesResponse getFareRulesById(Long fareRulesId);

    FareRulesResponse getFareRulesByFareId(Long fareId);

    List<FareRulesResponse> getFareRulesByAirlineId(Long airlineId);

    FareRulesResponse updateFareRules(Long fareRulesId, FareRulesRequest fareRulesRequest);

    void deleteFareRules(Long fareRulesId);
}
