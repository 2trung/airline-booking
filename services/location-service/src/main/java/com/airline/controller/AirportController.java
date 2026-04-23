package com.airline.controller;

import com.airline.dto.request.AirportRequest;
import com.airline.dto.response.AirportResponse;
import com.airline.exception.AirportException;
import com.airline.exception.CityException;
import com.airline.service.AirportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/airports")
public class AirportController {
    private final AirportService airportService;

    @PostMapping
    public ResponseEntity<AirportResponse> createAirport(@Valid @RequestBody AirportRequest request)
            throws AirportException, CityException {
        return ResponseEntity.status(HttpStatus.CREATED).body(airportService.createAirport(request));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AirportResponse>> createBulkAirports(
            @Valid @RequestBody List<AirportRequest> requests)
            throws AirportException, CityException {
        return ResponseEntity.status(HttpStatus.CREATED).body(airportService.createBulkAirports(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirportResponse> getAirportById(@PathVariable Long id) throws AirportException {
        return ResponseEntity.ok(airportService.getAirportById(id));
    }


    @GetMapping
    public ResponseEntity<List<AirportResponse>> getAllAirports(

    ) {
        return ResponseEntity.ok(airportService.getAllAirports());
    }

    @GetMapping("/city/{cityId}")
    public ResponseEntity<List<AirportResponse>> getAirportsByCityId(@PathVariable Long cityId) {
        return ResponseEntity.ok(airportService.getAirportsByCityId(cityId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AirportResponse> updateAirport(
            @PathVariable Long id,
            @Valid @RequestBody AirportRequest request) throws AirportException, CityException {
        return ResponseEntity.ok(airportService.updateAirport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable Long id) throws AirportException {
        airportService.deleteAirport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("search")
    public ResponseEntity<Page<AirportResponse>> searchAirports(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(airportService.searchAirports(keyword, pageable));
    }
}

