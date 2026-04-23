package com.airline.controller;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.exception.ResourceNotFoundException;
import com.airline.service.AircraftService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(
            @RequestBody AircraftRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(aircraftService.createAircraft(request, userId));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AircraftResponse>> createAircraftBulk(
            @RequestBody List<AircraftRequest> requests,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(aircraftService.createAircraftBulk(requests, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    @GetMapping
    public ResponseEntity<List<AircraftResponse>> listAllAircrafts(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(aircraftService.listAllAircraftsByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AircraftResponse>> searchAircrafts(
            @RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(aircraftService.searchAircrafts(keyword, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id,
            @RequestBody AircraftRequest request,
            @RequestHeader("X-User-Id") Long userId) throws ResourceNotFoundException {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id)
            throws ResourceNotFoundException {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }
}
