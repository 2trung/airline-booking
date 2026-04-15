package com.airline.service;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirlineService {

    AirlineResponse createAirline(AirlineRequest airlineRequest);

    AirlineResponse getAirlineByOwnerId(Long ownerId);

    AirlineResponse getAirlineById(Long id);

    Page<AirlineResponse> getAllAirlines(Pageable pageable);

    Page<AirlineResponse> getAllAirlinesWithKeyword(String keyword, Pageable pageable);

    AirlineResponse updateAirline(Long id, AirlineRequest airlineRequest);

    void deleteAirline(Long id);

    AirlineResponse changeAirlineStatus(Long id, String status);

    List<AirlineDropdownItem> getAirlinesForDropdown();

}
