package com.airline.service.impl;

import com.airline.dto.request.InsuranceCoverageRequest;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.entity.Ancillary;
import com.airline.entity.InsuranceCoverage;
import com.airline.mapper.InsuranceCoverageMapper;
import com.airline.repository.AncillaryRepository;
import com.airline.repository.InsuranceCoverageRepository;
import com.airline.service.InsuranceCoverageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InsuranceCoverageServiceImpl implements InsuranceCoverageService {

    private final AncillaryRepository ancillaryRepository;
    private final InsuranceCoverageRepository insuranceCoverageRepository;


    @Override
    public InsuranceCoverageResponse createInsuranceCoverage(InsuranceCoverageRequest insuranceCoverageRequest) {

        Ancillary ancillary = ancillaryRepository.findById(insuranceCoverageRequest.getAncillaryId())
                .orElseThrow(() -> new RuntimeException("Ancillary not found"));

        InsuranceCoverage coverage = InsuranceCoverageMapper.toEntity(
                insuranceCoverageRequest, ancillary
        );
        InsuranceCoverage savedCoverage = insuranceCoverageRepository.save(coverage);
        return InsuranceCoverageMapper.toResponse(savedCoverage);
    }

    @Override
    public InsuranceCoverageResponse getInsuranceCoverageById(Long id) {
        InsuranceCoverage insuranceCoverage = insuranceCoverageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found"));
        return InsuranceCoverageMapper.toResponse(insuranceCoverage);
    }

    @Override
    public InsuranceCoverageResponse updateInsuranceCoverage(Long id, InsuranceCoverageRequest insuranceCoverageRequest) {

        InsuranceCoverage insuranceCoverage = insuranceCoverageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found"));

        Ancillary ancillary = null;
        if (insuranceCoverage.getAncillary() == null) {
            throw new RuntimeException("Ancillary not found");
        }

        InsuranceCoverageMapper.updateEntityFromRequest(insuranceCoverage, insuranceCoverageRequest, ancillary);
        InsuranceCoverage updatedCoverage = insuranceCoverageRepository.save(insuranceCoverage);
        return InsuranceCoverageMapper.toResponse(updatedCoverage);
    }

    @Override
    public void deleteInsuranceCoverage(Long id) {
        InsuranceCoverage insuranceCoverage = insuranceCoverageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Insurance coverage not found"));
        insuranceCoverageRepository.delete(insuranceCoverage);


    }

    @Override
    public List<InsuranceCoverageResponse> getInsuranceCoveragesByAncillaryId(Long ancillaryId) {
        return insuranceCoverageRepository.findByAncillaryId(ancillaryId)
                .stream()
                .map(InsuranceCoverageMapper::toResponse)
                .toList();
    }

    @Override
    public List<InsuranceCoverageResponse> getActiveInsuranceCoveragesByAncillaryId(Long ancillaryId) {
        return insuranceCoverageRepository.findByAncillaryIdAndActiveTrue(ancillaryId)
                .stream()
                .map(InsuranceCoverageMapper::toResponse)
                .toList();
    }

    @Override
    public List<InsuranceCoverageResponse> getAllInsuranceCoverages() {
        return insuranceCoverageRepository.findAll()
                .stream()
                .map(InsuranceCoverageMapper::toResponse)
                .toList();
    }
}


