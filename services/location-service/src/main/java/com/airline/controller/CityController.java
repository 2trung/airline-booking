package com.airline.controller;

import com.airline.dto.request.CityRequest;
import com.airline.dto.response.CityResponse;
import com.airline.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/city")
public class CityController {
    private final CityService cityService;

    @PostMapping
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CityRequest cityRequest) throws Exception {
        CityResponse cityResponse = cityService.createCity(cityRequest);
        return ResponseEntity.ok(cityResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CityResponse> getCityById(@PathVariable Long id) throws Exception {
        CityResponse cityResponse = cityService.getCityById(id);
        return ResponseEntity.ok(cityResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CityResponse> updateCity(@PathVariable Long id, @Valid @RequestBody CityRequest cityRequest) throws Exception {
        CityResponse cityResponse = cityService.updateCity(id, cityRequest);
        return ResponseEntity.ok(cityResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) throws Exception {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllCities(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "name") String sortBy,
                                          @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(cityService.getAllCities(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCities(@RequestParam String keyword,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "name") String sortBy,
                                          @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(cityService.searchCities(keyword, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/country/{countryCode}")
    public ResponseEntity<?> getCityByCountryCode(@PathVariable String countryCode,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "name") String sortBy,
                                                  @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(cityService.getCityByCountryCode(countryCode, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByCityCode(@RequestParam String cityCode) {
        boolean exists = cityService.existsByCityCode(cityCode);
        return ResponseEntity.ok(exists);
    }
}
