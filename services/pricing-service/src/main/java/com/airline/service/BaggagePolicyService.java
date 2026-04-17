package com.airline.service;

import com.airline.dto.request.BaggagePolicyRequest;
import com.airline.dto.response.BaggagePolicyResponse;

import java.util.List;

public interface BaggagePolicyService {

    BaggagePolicyResponse createBaggagePolicy(BaggagePolicyRequest baggagePolicyRequest);

    BaggagePolicyResponse getBaggagePolicyById(Long baggagePolicyId);

    BaggagePolicyResponse getBaggagePolicyByFareId(Long fareId);

    List<BaggagePolicyResponse> getBaggagePoliciesByAirlineId(Long airlineId);

    BaggagePolicyResponse updateBaggagePolicy(Long baggagePolicyId, BaggagePolicyRequest baggagePolicyRequest);

    void deleteBaggagePolicy(Long baggagePolicyId);

}

