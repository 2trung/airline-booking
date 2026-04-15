package com.airline.controller;

import com.airline.dto.request.AircraftRequest;
import com.airline.dto.response.AircraftResponse;
import com.airline.service.AircraftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/aircrafts")
public class AircraftController {

    private final AircraftService aircraftService;

    @PostMapping
    public ResponseEntity<AircraftResponse> createAircraft(@Valid @RequestBody AircraftRequest aircraftRequest) {
        return ResponseEntity.ok(aircraftService.createAircraft(aircraftRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AircraftResponse> updateAircraft(
            @PathVariable Long id,
            @Valid @RequestBody AircraftRequest aircraftRequest
    ) {
        return ResponseEntity.ok(aircraftService.updateAircraft(id, aircraftRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable Long id) {
        aircraftService.deleteAircraft(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AircraftResponse> getAircraftById(@PathVariable Long id) {
        return ResponseEntity.ok(aircraftService.getAircraftById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AircraftResponse>> getAircraftsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(aircraftService.getAircraftsByOwnerId(ownerId));
    }

    @GetMapping("/owner/{ownerId}/paged")
    public ResponseEntity<Page<AircraftResponse>> getAircraftsByOwnerIdPaged(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(aircraftService.getAircraftsByOwnerId(ownerId, PageRequest.of(page, size, sort)));
    }

    @GetMapping
    public ResponseEntity<Page<AircraftResponse>> getAllAircrafts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(aircraftService.getAllAircrafts(PageRequest.of(page, size, sort)));
    }
}

