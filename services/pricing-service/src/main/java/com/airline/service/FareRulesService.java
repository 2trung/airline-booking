package com.airline.service;

import com.airline.dto.request.FareRulesRequest;
import com.airline.dto.response.FareRulesResponse;

import java.util.List;

public interface FareRulesService {
    FareRulesResponse createFareRules(FareRulesRequest request);
    FareRulesResponse getFareRulesById(Long id);
    FareRulesResponse getFareRulesByFareId(Long fareId);
    List<FareRulesResponse> getFareRulesByAirlineId(Long airlineId);
    FareRulesResponse updateFareRules(Long id, FareRulesRequest request);
    void deleteFareRules(Long id);
}
