package com.airline.controller;

import com.airline.dto.request.BaggagePolicyRequest;
import com.airline.dto.response.BaggagePolicyResponse;
import com.airline.service.BaggagePolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/baggage-policies")
@RequiredArgsConstructor
public class BaggagePolicyController {

    private final BaggagePolicyService baggagePolicyService;

    @PostMapping
    public ResponseEntity<BaggagePolicyResponse> createBaggagePolicy(
            @Valid @RequestBody BaggagePolicyRequest baggagePolicyRequest) {
        log.info("REST request to create baggage policy: {}", baggagePolicyRequest.getName());
        BaggagePolicyResponse response = baggagePolicyService.createBaggagePolicy(baggagePolicyRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{baggagePolicyId}")
    public ResponseEntity<BaggagePolicyResponse> getBaggagePolicyById(@PathVariable Long baggagePolicyId) {
        log.info("REST request to get baggage policy by ID: {}", baggagePolicyId);
        BaggagePolicyResponse response = baggagePolicyService.getBaggagePolicyById(baggagePolicyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fare/{fareId}")
    public ResponseEntity<BaggagePolicyResponse> getBaggagePolicyByFareId(@PathVariable Long fareId) {
        log.info("REST request to get baggage policy by fare ID: {}", fareId);
        BaggagePolicyResponse response = baggagePolicyService.getBaggagePolicyByFareId(fareId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<BaggagePolicyResponse>> getBaggagePoliciesByAirlineId(@PathVariable Long airlineId) {
        log.info("REST request to get baggage policies by airline ID: {}", airlineId);
        List<BaggagePolicyResponse> responses = baggagePolicyService.getBaggagePoliciesByAirlineId(airlineId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{baggagePolicyId}")
    public ResponseEntity<BaggagePolicyResponse> updateBaggagePolicy(
            @PathVariable Long baggagePolicyId,
            @Valid @RequestBody BaggagePolicyRequest baggagePolicyRequest) {
        log.info("REST request to update baggage policy with ID: {}", baggagePolicyId);
        BaggagePolicyResponse response = baggagePolicyService.updateBaggagePolicy(baggagePolicyId, baggagePolicyRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{baggagePolicyId}")
    public ResponseEntity<Void> deleteBaggagePolicy(@PathVariable Long baggagePolicyId) {
        log.info("REST request to delete baggage policy with ID: {}", baggagePolicyId);
        baggagePolicyService.deleteBaggagePolicy(baggagePolicyId);
        return ResponseEntity.noContent().build();
    }
}
