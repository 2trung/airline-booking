package com.airline.repository;

import com.airline.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FlightRepository extends JpaRepository<Flight, Long> {

    boolean existsByFlightNumberIgnoreCase(String flightNumber);

    boolean existsByFlightNumberIgnoreCaseAndIdNot(String flightNumber, Long id);

    @Query("""
            SELECT f FROM Flight f
            WHERE (:airlineId IS NULL OR f.airlineId = :airlineId)
              AND (:departureAirportId IS NULL OR f.departureAirportId = :departureAirportId)
              AND (:arrivalAirportId IS NULL OR f.arrivalAirportId = :arrivalAirportId)
            """)
    Page<Flight> searchFlights(
            @Param("airlineId") Long airlineId,
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            Pageable pageable
    );
}

