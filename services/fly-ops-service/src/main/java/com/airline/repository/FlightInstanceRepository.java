package com.airline.repository;

import com.airline.entity.FlightInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface FlightInstanceRepository extends JpaRepository<FlightInstance, Long> {

    @Query("""
            SELECT fi FROM FlightInstance fi
            WHERE (:airlineId IS NULL OR fi.airlineId = :airlineId)
              AND (:departureAirportId IS NULL OR fi.departureAirportId = :departureAirportId)
              AND (:arrivalAirportId IS NULL OR fi.arrivalAirportId = :arrivalAirportId)
              AND (:flightId IS NULL OR fi.flight.id = :flightId)
              AND (:departureTime IS NULL OR fi.departureTime = :departureTime)
              AND (:fromDateTime IS NULL OR fi.departureTime >= :fromDateTime)
              AND (:toDateTime IS NULL OR fi.departureTime < :toDateTime)
            """)
    Page<FlightInstance> searchFlightInstances(
            @Param("airlineId") Long airlineId,
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            @Param("flightId") Long flightId,
            @Param("departureTime") LocalDateTime departureTime,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Pageable pageable
    );
}

