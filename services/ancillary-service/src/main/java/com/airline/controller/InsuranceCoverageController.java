package com.airline.controller;

import com.airline.dto.request.InsuranceCoverageRequest;
import com.airline.dto.response.InsuranceCoverageResponse;
import com.airline.service.InsuranceCoverageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/insurance-coverage")
@RequiredArgsConstructor
public class InsuranceCoverageController {

    private final InsuranceCoverageService insuranceCoverageService;

    @PostMapping
    public ResponseEntity<InsuranceCoverageResponse> createInsuranceCoverage(
            @Valid @RequestBody InsuranceCoverageRequest insuranceCoverageRequest) {
        log.info("REST request to create insurance coverage: {}", insuranceCoverageRequest.getName());
        InsuranceCoverageResponse response = insuranceCoverageService.createInsuranceCoverage(insuranceCoverageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{insuranceCoverageId}")
    public ResponseEntity<InsuranceCoverageResponse> getInsuranceCoverageById(@PathVariable Long insuranceCoverageId) {
        log.info("REST request to get insurance coverage by ID: {}", insuranceCoverageId);
        InsuranceCoverageResponse response = insuranceCoverageService.getInsuranceCoverageById(insuranceCoverageId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{insuranceCoverageId}")
    public ResponseEntity<InsuranceCoverageResponse> updateInsuranceCoverage(
            @PathVariable Long insuranceCoverageId,
            @Valid @RequestBody InsuranceCoverageRequest insuranceCoverageRequest) {
        log.info("REST request to update insurance coverage with ID: {}", insuranceCoverageId);
        InsuranceCoverageResponse response = insuranceCoverageService.updateInsuranceCoverage(insuranceCoverageId, insuranceCoverageRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{insuranceCoverageId}")
    public ResponseEntity<Void> deleteInsuranceCoverage(@PathVariable Long insuranceCoverageId) {
        log.info("REST request to delete insurance coverage with ID: {}", insuranceCoverageId);
        insuranceCoverageService.deleteInsuranceCoverage(insuranceCoverageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ancillary/{ancillaryId}")
    public ResponseEntity<List<InsuranceCoverageResponse>> getInsuranceCoveragesByAncillaryId(@PathVariable Long ancillaryId) {
        log.info("REST request to get insurance coverages by ancillary ID: {}", ancillaryId);
        List<InsuranceCoverageResponse> responses = insuranceCoverageService.getInsuranceCoveragesByAncillaryId(ancillaryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ancillary/{ancillaryId}/active")
    public ResponseEntity<List<InsuranceCoverageResponse>> getActiveInsuranceCoveragesByAncillaryId(@PathVariable Long ancillaryId) {
        log.info("REST request to get active insurance coverages by ancillary ID: {}", ancillaryId);
        List<InsuranceCoverageResponse> responses = insuranceCoverageService.getActiveInsuranceCoveragesByAncillaryId(ancillaryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping
    public ResponseEntity<List<InsuranceCoverageResponse>> getAllInsuranceCoverages() {
        log.info("REST request to get all insurance coverages");
        List<InsuranceCoverageResponse> responses = insuranceCoverageService.getAllInsuranceCoverages();
        return ResponseEntity.ok(responses);
    }
}
