package com.airline.service;

import com.airline.dto.request.FareRequest;
import com.airline.dto.response.FareResponse;
import com.airline.entity.Fare;

import java.util.List;
import java.util.Map;

public interface FareService {
    FareResponse createFare(FareRequest request);
    List<FareResponse> createFares(List<FareRequest> requests);
    FareResponse getFareById(Long id);
    List<FareResponse> getFaresByFlightIdAndCabinClassId(
            Long flightId,
            Long cabinClassId
    );
    FareResponse updateFare(
            Long id,
            FareRequest request
    );
    void deleteFare(Long id);

    List<Fare> getFares();

    Map<Long, FareResponse> getLowestFarePerFlight(
            List<Long> flightIds, Long cabinClassId);

    FareResponse getLowestFareForFlightAndCabin(Long flightId, Long cabinClassId);

    Map<Long, FareResponse> getFaresByIds(List<Long> ids);
}
