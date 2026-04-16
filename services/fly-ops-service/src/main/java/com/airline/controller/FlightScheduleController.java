package com.airline.controller;

import com.airline.dto.request.FlightScheduleRequest;
import com.airline.dto.response.FlightScheduleResponse;
import com.airline.service.FlightScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flight-schedules")
public class FlightScheduleController {

    private final FlightScheduleService flightScheduleService;

    @PostMapping
    public ResponseEntity<FlightScheduleResponse> createFlightSchedule(
            @Valid @RequestBody FlightScheduleRequest flightScheduleRequest
    ) {
        return ResponseEntity.ok(flightScheduleService.createFlightSchedule(flightScheduleRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightScheduleResponse> getFlightScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(flightScheduleService.getFlightScheduleById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateFlightSchedule(
            @PathVariable Long id,
            @Valid @RequestBody FlightScheduleRequest flightScheduleRequest
    ) {
        flightScheduleService.updateFlightSchedule(id, flightScheduleRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightSchedule(@PathVariable Long id) {
        flightScheduleService.deleteFlightSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<FlightScheduleResponse>> getFlightSchedulesByAirline(
            @PathVariable Long airlineId
    ) {
        return ResponseEntity.ok(flightScheduleService.getFlightSchedulesByAirline(airlineId));
    }
}
