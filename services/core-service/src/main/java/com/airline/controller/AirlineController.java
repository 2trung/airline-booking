package com.airline.controller;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import com.airline.service.AirlineService;
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
@RequestMapping("/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping
    public ResponseEntity<AirlineResponse> createAirline(@Valid @RequestBody AirlineRequest airlineRequest) {
        return ResponseEntity.ok(airlineService.createAirline(airlineRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirlineResponse> getAirlineById(@PathVariable Long id) {
        return ResponseEntity.ok(airlineService.getAirlineById(id));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<AirlineResponse> getAirlineByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(airlineService.getAirlineByOwnerId(ownerId));
    }

    @GetMapping
    public ResponseEntity<Page<AirlineResponse>> getAllAirlines(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(airlineService.getAllAirlines(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AirlineResponse>> getAllAirlinesWithKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(airlineService.getAllAirlinesWithKeyword(keyword, PageRequest.of(page, size, sort)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirlineResponse> updateAirline(
            @PathVariable Long id,
            @Valid @RequestBody AirlineRequest airlineRequest
    ) {
        return ResponseEntity.ok(airlineService.updateAirline(id, airlineRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirline(@PathVariable Long id) {
        airlineService.deleteAirline(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AirlineResponse> changeAirlineStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(airlineService.changeAirlineStatus(id, status));
    }

    @GetMapping("/dropdown")
    public ResponseEntity<List<AirlineDropdownItem>> getAirlinesForDropdown() {
        return ResponseEntity.ok(airlineService.getAirlinesForDropdown());
    }
}
