package com.airline.controller;

import com.airline.dto.request.SeatMapRequest;
import com.airline.dto.response.SeatMapResponse;
import com.airline.service.SeatMapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat-maps")
public class SeatMapController {

    private final SeatMapService seatMapService;

    @PostMapping
    public ResponseEntity<SeatMapResponse> createSeatMap(@Valid @RequestBody SeatMapRequest seatMapRequest) {
        return ResponseEntity.ok(seatMapService.createSeatMap(seatMapRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatMapResponse> getSeatMapById(@PathVariable Long id) {
        return ResponseEntity.ok(seatMapService.getSeatMapById(id));
    }

    @GetMapping("/cabin-class/{cabinClassId}")
    public ResponseEntity<SeatMapResponse> getSeatMapByCabinClassId(@PathVariable Long cabinClassId) {
        return ResponseEntity.ok(seatMapService.getSeatMapByCabinClassId(cabinClassId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeatMapResponse> updateSeatMap(
            @PathVariable Long id,
            @Valid @RequestBody SeatMapRequest seatMapRequest
    ) {
        return ResponseEntity.ok(seatMapService.updateSeatMap(id, seatMapRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeatMap(@PathVariable Long id) {
        seatMapService.deleteSeatMap(id);
        return ResponseEntity.noContent().build();
    }
}

