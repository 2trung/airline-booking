package com.airline.controller;

import com.airline.dto.request.FlightInstanceRequest;
import com.airline.dto.response.FlightInstanceResponse;
import com.airline.service.FlightInstanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flight-instances")
public class FlightInstanceController {

    private final FlightInstanceService flightInstanceService;

    @PostMapping
    public ResponseEntity<FlightInstanceResponse> createFlightInstance(
            @Valid @RequestBody FlightInstanceRequest flightInstanceRequest
    ) {
        return ResponseEntity.ok(flightInstanceService.createFlightInstance(flightInstanceRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightInstanceResponse> getFlightInstanceById(@PathVariable Long id) {
        return ResponseEntity.ok(flightInstanceService.getFlightInstanceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightInstanceResponse> updateFlightInstance(
            @PathVariable Long id,
            @Valid @RequestBody FlightInstanceRequest flightInstanceRequest
    ) {
        return ResponseEntity.ok(flightInstanceService.updateFlightInstance(id, flightInstanceRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightInstance(@PathVariable Long id) {
        flightInstanceService.deleteFlightInstance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FlightInstanceResponse>> searchFlightInstances(
            @RequestParam(required = false) Long airlineId,
            @RequestParam(required = false) Long departureAirportId,
            @RequestParam(required = false) Long arrivalAirportId,
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) Long onDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "departureTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(
                flightInstanceService.getByAirlineId(
                        airlineId,
                        departureAirportId,
                        arrivalAirportId,
                        flightId,
                        departureTime,
                        onDate,
                        PageRequest.of(page, size, sort)
                )
        );
    }
}

