package com.airline.service;

import com.airline.dto.request.CabinClassRequest;
import com.airline.dto.response.CabinClassResponse;

import java.util.List;

public interface CabinClassService {
    CabinClassResponse createCabinClass(CabinClassRequest cabinClassRequest);

    CabinClassResponse getCabinClassById(Long id);

    List<CabinClassResponse> getAllCabinClassesByAircraftId(Long aircraftId);

    CabinClassResponse getByAircraftIdAndName(Long aircraftId, String name);

    CabinClassResponse updateCabinClass(Long id, CabinClassRequest cabinClassRequest);

    void deleteCabinClass(Long id);
}
