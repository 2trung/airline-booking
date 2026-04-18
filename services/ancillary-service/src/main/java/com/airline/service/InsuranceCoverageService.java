package com.airline.service;

import com.airline.dto.request.InsuranceCoverageRequest;
import com.airline.dto.response.InsuranceCoverageResponse;

import java.util.List;

public interface InsuranceCoverageService {
    InsuranceCoverageResponse createInsuranceCoverage(InsuranceCoverageRequest insuranceCoverageRequest);

    InsuranceCoverageResponse getInsuranceCoverageById(Long id);

    InsuranceCoverageResponse updateInsuranceCoverage(Long id, InsuranceCoverageRequest insuranceCoverageRequest);

    void deleteInsuranceCoverage(Long id);

    List<InsuranceCoverageResponse> getInsuranceCoveragesByAncillaryId(Long ancillaryId);

    List<InsuranceCoverageResponse> getActiveInsuranceCoveragesByAncillaryId(Long ancillaryId);

    List<InsuranceCoverageResponse> getAllInsuranceCoverages();
}
