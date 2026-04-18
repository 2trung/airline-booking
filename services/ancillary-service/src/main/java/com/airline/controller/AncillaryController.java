package com.airline.controller;

import com.airline.dto.request.AncillaryRequest;
import com.airline.dto.response.AncillaryResponse;
import com.airline.service.AncillaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ancillary")
@RequiredArgsConstructor
public class AncillaryController {
    private final AncillaryService ancillaryService;

    @RequestMapping("/{id}")
    public ResponseEntity<AncillaryResponse> getAncillaryById(Long id) throws Exception {
        AncillaryResponse response = ancillaryService.getAncillaryById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AncillaryResponse> createAncillary(@Valid @RequestBody AncillaryRequest ancillaryRequest,
                                                             @RequestHeader("X-Airline-Id") Long airlineId) {
        AncillaryResponse response = ancillaryService.createAncillary(airlineId, ancillaryRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AncillaryResponse> updateAncillary(@PathVariable Long id,
                                                             @RequestBody AncillaryRequest ancillaryRequest
                                                             ) throws Exception {
        AncillaryResponse response = ancillaryService.updateAncillary(id, ancillaryRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAncillary(@PathVariable Long id) throws Exception {
        ancillaryService.deleteAncillary(id);
        return ResponseEntity.noContent().build();
    }
}
