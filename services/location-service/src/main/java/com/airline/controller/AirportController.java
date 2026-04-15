package com.airline.controller;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/airport")
public class AirportController {
    private final AirportService airportService;

    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(@Valid @RequestBody AirportRequest airportRequest) throws Exception {
        AirportResponse airportResponse = airportService.createAirport(airportRequest);
        return ResponseEntity.ok(airportResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportResponse> getAirportById(@PathVariable Long id) throws Exception {
        AirportResponse airportResponse = airportService.getAirportById(id);
        return ResponseEntity.ok(airportResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportResponse> updateAirport(@PathVariable Long id, @Valid @RequestBody AirportRequest airportRequest) throws Exception {
        AirportResponse airportResponse = airportService.updateAirport(id, airportRequest);
        return ResponseEntity.ok(airportResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) throws Exception {
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllAirports(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "name") String sortBy,
                                            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(airportService.getAllAirports(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAirports(@RequestParam String keyword,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "name") String sortBy,
                                            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(airportService.searchAirports(keyword, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<?> getAirportsByCity(@PathVariable Long cityId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "name") String sortBy,
                                               @RequestParam(defaultValue = "asc") String sortDir
    ) throws Exception {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(airportService.getAirportsByCity(cityId, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByIataCode(@RequestParam String iataCode) {
        boolean exists = airportService.existsByIataCode(iataCode);
        return ResponseEntity.ok(exists);
    }
}

