package com.airline.controller;

import com.airline.dto.request.FareRequest;
import com.airline.dto.response.FareResponse;
import com.airline.service.FareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fares")
@RequiredArgsConstructor
public class FareController {

    private final FareService fareService;

    @PostMapping
    public ResponseEntity<FareResponse> createFare(@Valid @RequestBody FareRequest fareRequest) {
        log.info("REST request to create fare for flight: {}", fareRequest.getFlightId());
        FareResponse fareResponse = fareService.createFare(fareRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(fareResponse);
    }

    @GetMapping("/{fareId}")
    public ResponseEntity<FareResponse> getFareById(@PathVariable Long fareId) {
        log.info("REST request to get fare by ID: {}", fareId);
        FareResponse fareResponse = fareService.getFareById(fareId);
        return ResponseEntity.ok(fareResponse);
    }

    @GetMapping
    public ResponseEntity<List<FareResponse>> getFares(
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) Long cabinClassId) {
        log.info("REST request to get fares with flightId: {}, cabinClassId: {}", flightId, cabinClassId);

        if (flightId != null && cabinClassId != null) {
            List<FareResponse> fares = fareService.getFaresByFlightIdAndCabinClassId(flightId, cabinClassId);
            return ResponseEntity.ok(fares);
        }

        List<FareResponse> allFares = fareService.getFares();
        return ResponseEntity.ok(allFares);
    }

    @PutMapping("/{fareId}")
    public ResponseEntity<FareResponse> updateFare(
            @PathVariable Long fareId,
            @Valid @RequestBody FareRequest fareRequest) {
        log.info("REST request to update fare with ID: {}", fareId);
        FareResponse fareResponse = fareService.updateFare(fareId, fareRequest);
        return ResponseEntity.ok(fareResponse);
    }

    @DeleteMapping("/{fareId}")
    public ResponseEntity<Void> deleteFare(@PathVariable Long fareId) {
        log.info("REST request to delete fare with ID: {}", fareId);
        fareService.deleteFare(fareId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lowest")
    public ResponseEntity<Map<Long, FareResponse>> getLowestFareByFlight(
            @RequestParam List<Long> flightIds,
            @RequestParam Long cabinClassId) {
        log.info("REST request to get lowest fares for {} flights", flightIds.size());
        Map<Long, FareResponse> lowestFares = fareService.getLowestFareByFlight(flightIds, cabinClassId);
        return ResponseEntity.ok(lowestFares);
    }

    @GetMapping("/batch")
    public ResponseEntity<Map<Long, FareResponse>> getFaresByIds(@RequestParam List<Long> fareIds) {
        log.info("REST request to get fares by IDs: {}", fareIds);
        Map<Long, FareResponse> fares = fareService.getFaresByIds(fareIds);
        return ResponseEntity.ok(fares);
    }
}
