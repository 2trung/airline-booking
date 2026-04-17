package com.airline.service;

import com.airline.dto.request.FareRequest;
import com.airline.dto.response.FareResponse;

import java.util.List;
import java.util.Map;

public interface FareService {
    FareResponse createFare(FareRequest fareRequest);

    FareResponse getFareById(Long fareId);

    List<FareResponse> getFaresByFlightIdAndCabinClassId(Long flightId, Long cabinClassId);

    FareResponse updateFare(Long fareId, FareRequest fareRequest);

    void deleteFare(Long fareId);

    List<FareResponse> getFares();

    Map<Long, FareResponse> getLowestFareByFlight(
            List<Long> flightIds, Long cabinClassId
    );

    Map<Long, FareResponse> getFaresByIds(List<Long> fareIds);
}
