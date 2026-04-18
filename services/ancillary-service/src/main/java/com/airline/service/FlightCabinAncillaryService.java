package com.airline.service;

import com.airline.dto.request.FlightCabinAncillaryRequest;
import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.enums.AncillaryType;

import java.util.List;

public interface FlightCabinAncillaryService {

    FlightCabinAncillaryResponse createFlightCabinAncillary(FlightCabinAncillaryRequest flightCabinAncillaryRequest);

    FlightCabinAncillaryResponse getById(Long id);

    void deleteById(Long id);

    List<FlightCabinAncillaryResponse> getByFlightAndCabinClass(Long flightId, Long cabinClassId);

    List<FlightCabinAncillaryResponse> getAllByIds(List<Long> ids);

    FlightCabinAncillaryResponse getByFlightIdAndCabinClassIdAndType(
            Long flightId, Long cabinClassId, AncillaryType type
    );

    List<FlightCabinAncillaryResponse> getAllByFlightIdAndCabinClassIdAndType(
            Long flightId, Long cabinClassId, AncillaryType type
    );

    FlightCabinAncillaryResponse update(Long id, FlightCabinAncillaryRequest flightCabinAncillaryRequest);

    Double calculateAncillaryPrice(List<Long> ancillaryIds);
}
