package com.airline.service;

import com.airline.dto.request.FlightCabinAncillaryRequest;
import com.airline.dto.response.FlightCabinAncillaryResponse;
import com.airline.enums.AncillaryType;
import com.airline.exception.ResourceNotFoundException;

import java.util.List;

public interface FlightCabinAncillaryService {

    FlightCabinAncillaryResponse create(FlightCabinAncillaryRequest request) throws ResourceNotFoundException;

    List<FlightCabinAncillaryResponse> bulkCreate(List<FlightCabinAncillaryRequest> requests) throws ResourceNotFoundException;

    FlightCabinAncillaryResponse getById(Long id) throws ResourceNotFoundException;

    List<FlightCabinAncillaryResponse> getAllByFlightAndCabinClass(Long flightId, Long cabinClassId);

    List<FlightCabinAncillaryResponse> getAllByIds(List<Long> ids);

    FlightCabinAncillaryResponse getByFlightIdAndCabinClassAndType(Long flightId, Long cabinClassId, AncillaryType type) throws ResourceNotFoundException;

    List<FlightCabinAncillaryResponse> getAllByFlightIdAndCabinClassAndType(Long flightId, Long cabinClassId, AncillaryType type) throws ResourceNotFoundException;

    FlightCabinAncillaryResponse update(Long id, FlightCabinAncillaryRequest request) throws ResourceNotFoundException;

    void delete(Long id);

    Double calculateAncillaryPrice(List<Long> ancillaryIds);
}
