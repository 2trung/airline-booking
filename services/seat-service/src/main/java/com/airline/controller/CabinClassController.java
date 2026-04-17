package com.airline.controller;

import com.airline.dto.request.CabinClassRequest;
import com.airline.dto.response.CabinClassResponse;
import com.airline.service.CabinClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cabin-classes")
public class CabinClassController {

    private final CabinClassService cabinClassService;

    @PostMapping
    public ResponseEntity<CabinClassResponse> createCabinClass(@Valid @RequestBody CabinClassRequest cabinClassRequest) {
        return ResponseEntity.ok(cabinClassService.createCabinClass(cabinClassRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CabinClassResponse> getCabinClassById(@PathVariable Long id) {
        return ResponseEntity.ok(cabinClassService.getCabinClassById(id));
    }

    @GetMapping("/aircraft/{aircraftId}")
    public ResponseEntity<List<CabinClassResponse>> getAllCabinClassesByAircraftId(@PathVariable Long aircraftId) {
        return ResponseEntity.ok(cabinClassService.getAllCabinClassesByAircraftId(aircraftId));
    }

    @GetMapping("/aircraft/{aircraftId}/name/{name}")
    public ResponseEntity<CabinClassResponse> getByAircraftIdAndName(
            @PathVariable Long aircraftId,
            @PathVariable String name
    ) {
        return ResponseEntity.ok(cabinClassService.getByAircraftIdAndName(aircraftId, name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CabinClassResponse> updateCabinClass(
            @PathVariable Long id,
            @Valid @RequestBody CabinClassRequest cabinClassRequest
    ) {
        return ResponseEntity.ok(cabinClassService.updateCabinClass(id, cabinClassRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCabinClass(@PathVariable Long id) {
        cabinClassService.deleteCabinClass(id);
        return ResponseEntity.noContent().build();
    }
}
