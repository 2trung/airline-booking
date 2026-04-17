package com.airline.controller;

import com.airline.dto.request.FareRulesRequest;
import com.airline.dto.response.FareRulesResponse;
import com.airline.service.FareRulesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fare-rules")
@RequiredArgsConstructor
public class FareRulesController {

    private final FareRulesService fareRulesService;

    @PostMapping
    public ResponseEntity<FareRulesResponse> createFareRules(@Valid @RequestBody FareRulesRequest fareRulesRequest) {
        log.info("REST request to create fare rules: {}", fareRulesRequest.getRuleName());
        FareRulesResponse fareRulesResponse = fareRulesService.createFareRules(fareRulesRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(fareRulesResponse);
    }

    @GetMapping("/{fareRulesId}")
    public ResponseEntity<FareRulesResponse> getFareRulesById(@PathVariable Long fareRulesId) {
        log.info("REST request to get fare rules by ID: {}", fareRulesId);
        FareRulesResponse fareRulesResponse = fareRulesService.getFareRulesById(fareRulesId);
        return ResponseEntity.ok(fareRulesResponse);
    }

    @GetMapping("/fare/{fareId}")
    public ResponseEntity<FareRulesResponse> getFareRulesByFareId(@PathVariable Long fareId) {
        log.info("REST request to get fare rules by fare ID: {}", fareId);
        FareRulesResponse fareRulesResponse = fareRulesService.getFareRulesByFareId(fareId);
        return ResponseEntity.ok(fareRulesResponse);
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<FareRulesResponse>> getFareRulesByAirlineId(@PathVariable Long airlineId) {
        log.info("REST request to get fare rules by airline ID: {}", airlineId);
        List<FareRulesResponse> fareRulesList = fareRulesService.getFareRulesByAirlineId(airlineId);
        return ResponseEntity.ok(fareRulesList);
    }

    @PutMapping("/{fareRulesId}")
    public ResponseEntity<FareRulesResponse> updateFareRules(
            @PathVariable Long fareRulesId,
            @Valid @RequestBody FareRulesRequest fareRulesRequest) {
        log.info("REST request to update fare rules with ID: {}", fareRulesId);
        FareRulesResponse fareRulesResponse = fareRulesService.updateFareRules(fareRulesId, fareRulesRequest);
        return ResponseEntity.ok(fareRulesResponse);
    }

    @DeleteMapping("/{fareRulesId}")
    public ResponseEntity<Void> deleteFareRules(@PathVariable Long fareRulesId) {
        log.info("REST request to delete fare rules with ID: {}", fareRulesId);
        fareRulesService.deleteFareRules(fareRulesId);
        return ResponseEntity.noContent().build();
    }
}
