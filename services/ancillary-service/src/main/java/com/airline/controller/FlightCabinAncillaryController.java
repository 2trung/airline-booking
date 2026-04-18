package com.airline.controller;

import com.airline.dto.request.FlightCabinAncillaryRequest;
import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.enums.AncillaryType;
import com.airline.service.FlightCabinAncillaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/flight-cabin-ancillary")
@RequiredArgsConstructor
public class FlightCabinAncillaryController {

    private final FlightCabinAncillaryService flightCabinAncillaryService;

    @PostMapping
    public ResponseEntity<FlightCabinAncillaryResponse> create(
            @Valid @RequestBody FlightCabinAncillaryRequest flightCabinAncillaryRequest
    ) {
        log.info("REST request to create flight cabin ancillary for flight ID: {} and cabin class ID: {}",
                flightCabinAncillaryRequest.getFlightId(),
                flightCabinAncillaryRequest.getCabinClassId());
        FlightCabinAncillaryResponse response = flightCabinAncillaryService.createFlightCabinAncillary(flightCabinAncillaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightCabinAncillaryResponse> getById(@PathVariable Long id) {
        log.info("REST request to get flight cabin ancillary by ID: {}", id);
        FlightCabinAncillaryResponse response = flightCabinAncillaryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("REST request to delete flight cabin ancillary by ID: {}", id);
        flightCabinAncillaryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightCabinAncillaryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FlightCabinAncillaryRequest flightCabinAncillaryRequest
    ) {
        log.info("REST request to update flight cabin ancillary by ID: {}", id);
        FlightCabinAncillaryResponse response = flightCabinAncillaryService.update(id, flightCabinAncillaryRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightCabinAncillaryResponse>> getByFlightAndCabinClass(
            @RequestParam Long flightId,
            @RequestParam Long cabinClassId
    ) {
        log.info("REST request to get flight cabin ancillaries by flight ID: {} and cabin class ID: {}", flightId, cabinClassId);
        List<FlightCabinAncillaryResponse> response = flightCabinAncillaryService.getByFlightAndCabinClass(flightId, cabinClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/batch")
    public ResponseEntity<List<FlightCabinAncillaryResponse>> getAllByIds(@RequestParam List<Long> ids) {
        log.info("REST request to get flight cabin ancillaries by IDs: {}", ids);
        List<FlightCabinAncillaryResponse> response = flightCabinAncillaryService.getAllByIds(ids);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type")
    public ResponseEntity<FlightCabinAncillaryResponse> getByFlightIdAndCabinClassIdAndType(
            @RequestParam Long flightId,
            @RequestParam Long cabinClassId,
            @RequestParam AncillaryType type
    ) {
        log.info("REST request to get flight cabin ancillary by flight ID: {}, cabin class ID: {}, and type: {}",
                flightId, cabinClassId, type);
        FlightCabinAncillaryResponse response = flightCabinAncillaryService
                .getByFlightIdAndCabinClassIdAndType(flightId, cabinClassId, type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/types")
    public ResponseEntity<List<FlightCabinAncillaryResponse>> getAllByFlightIdAndCabinClassIdAndType(
            @RequestParam Long flightId,
            @RequestParam Long cabinClassId,
            @RequestParam AncillaryType type
    ) {
        log.info("REST request to get all flight cabin ancillaries by flight ID: {}, cabin class ID: {}, and type: {}",
                flightId, cabinClassId, type);
        List<FlightCabinAncillaryResponse> response = flightCabinAncillaryService
                .getAllByFlightIdAndCabinClassIdAndType(flightId, cabinClassId, type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/price")
    public ResponseEntity<Double> calculateAncillaryPrice(@RequestParam List<Long> ancillaryIds) {
        log.info("REST request to calculate ancillary price for IDs: {}", ancillaryIds);
        Double totalPrice = flightCabinAncillaryService.calculateAncillaryPrice(ancillaryIds);
        return ResponseEntity.ok(totalPrice);
    }
}

