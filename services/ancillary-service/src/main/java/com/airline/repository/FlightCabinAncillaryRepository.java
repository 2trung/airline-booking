package com.airline.repository;

import com.airline.entity.FlightCabinAncillary;
import com.airline.enums.AncillaryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightCabinAncillaryRepository extends JpaRepository<FlightCabinAncillary, Long> {

    List<FlightCabinAncillary> findByFlightIdAndCabinClassId(Long flightId, Long cabinClassId);

    FlightCabinAncillary findByFlightIdAndCabinClassIdAndAncillary_Type(
            Long flightId, Long cabinClassId, AncillaryType type
    );

    List<FlightCabinAncillary> findAllByFlightIdAndCabinClassIdAndAncillary_Type(
            Long flightId, Long cabinClassId, AncillaryType type
    );
}

