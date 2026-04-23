package com.airline.service;

import com.airline.dto.request.AirlineRequest;
import com.airline.dto.response.AirlineDropdownItem;
import com.airline.dto.response.AirlineResponse;
import com.airline.enums.AirlineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirlineService {
    AirlineResponse createAirline(AirlineRequest request, Long ownerId);
    AirlineResponse getAirlineByOwner(Long ownerId);
    AirlineResponse getAirlineById(Long id);
    Page<AirlineResponse> getAllAirlines(Pageable pageable);
    Page<AirlineResponse> searchAirlines(String keyword, Pageable pageable);
    AirlineResponse updateAirline(AirlineRequest request, Long ownerId);
    void deleteAirline(Long id, Long ownerId);

    AirlineResponse changeStatusByAdmin(Long airlineId, AirlineStatus status);

    List<AirlineDropdownItem> getAirlinesForDropdown();

}
