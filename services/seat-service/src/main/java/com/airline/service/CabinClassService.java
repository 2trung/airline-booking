package com.airline.service;

import com.airline.dto.request.CabinClassRequest;
import com.airline.dto.response.CabinClassResponse;
import com.airline.enums.CabinClassType;

import java.util.List;

public interface CabinClassService {
    CabinClassResponse createCabinClass(CabinClassRequest request);
    List<CabinClassResponse> createCabinClasses(List<CabinClassRequest> requests);
    CabinClassResponse getCabinClassById(Long id);
    List<CabinClassResponse> getCabinClassesByAircraftId(
            Long aircraftId);
    CabinClassResponse getByAircraftIdAndName(Long aircraftId,
                                              CabinClassType name);
    CabinClassResponse updateCabinClass(Long id, CabinClassRequest request);
    void deleteCabinClass(Long id);
}
