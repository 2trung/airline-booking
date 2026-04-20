package com.airline.controller;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;
import com.airline.exception.ResourceNotFoundException;
import com.airline.service.AncillaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ancillary")
@RequiredArgsConstructor
public class AncillaryController {
    private final AncillaryService ancillaryService;

    @PostMapping
    public ResponseEntity<AncillaryResponse> create(@Valid @RequestBody AncillaryRequest request, @RequestHeader("X-User-Id") Long userId) throws ResourceNotFoundException {
        return ResponseEntity.ok(ancillaryService.create(userId, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AncillaryResponse> getById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(ancillaryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AncillaryResponse>> getAllByAirlineId(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ancillaryService.getAllByAirlineId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AncillaryResponse> update(@PathVariable Long id, @Valid @RequestBody AncillaryRequest request) throws ResourceNotFoundException {
        return ResponseEntity.ok(ancillaryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ancillaryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
