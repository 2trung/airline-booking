package com.airline.repository;

import com.airline.entity.FlightInstance;
import com.airline.enums.FlightStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FlightInstanceRepository extends JpaRepository<FlightInstance, Long> {

    @Query("SELECT fi FROM FlightInstance fi WHERE fi.airlineId = :airlineId" +
            " AND (:departureAirportId IS NULL OR fi.departureAirportId = :departureAirportId)" +
            " AND (:arrivalAirportId IS NULL OR fi.arrivalAirportId = :arrivalAirportId)" +
            " AND (:flightId IS NULL OR fi.flight.id = :flightId)" +
            " AND (:dayStart IS NULL OR fi.departureDateTime >= :dayStart)" +
            " AND (:dayEnd IS NULL OR fi.departureDateTime < :dayEnd)")
    Page<FlightInstance> findByAirlineIdWithFilters(
            @Param("airlineId") Long airlineId,
            @Param("departureAirportId") Long departureAirportId,
            @Param("arrivalAirportId") Long arrivalAirportId,
            @Param("flightId") Long flightId,
            @Param("dayStart") Instant dayStart,
            @Param("dayEnd") Instant dayEnd,
            Pageable pageable);

    Page<FlightInstance> findByStatus(FlightStatus status, Pageable pageable);

    @Query("SELECT fi FROM FlightInstance fi WHERE fi.departureAirportId = :depId AND fi.arrivalAirportId = :arrId AND fi.departureDateTime >= :fromDate AND fi.departureDateTime <= :toDate AND fi.status = 'SCHEDULED'")
    List<FlightInstance> searchFlights(
            @Param("depId") Long departureAirportId,
            @Param("arrId") Long arrivalAirportId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate);

    @Query("SELECT fi FROM FlightInstance fi WHERE fi.departureAirportId = :depId AND fi.arrivalAirportId = :arrId AND fi.departureDateTime >= :fromDate AND fi.departureDateTime <= :toDate")
    Page<FlightInstance> searchFlightsPaged(
            @Param("depId") Long departureAirportId,
            @Param("arrId") Long arrivalAirportId,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT fi FROM FlightInstance fi WHERE fi.id = :id")
    Optional<FlightInstance> findByIdForUpdate(@Param("id") Long id);

    Long countByFlightIdAndStatus(Long flightId, FlightStatus status);

    @Query("SELECT fi FROM FlightInstance fi JOIN FETCH fi.flight WHERE fi.id IN :ids")
    List<FlightInstance> findAllByIdInWithFlight(@Param("ids") Collection<Long> ids);

    Page<FlightInstance> findAll(Specification<FlightInstance> spec, Pageable sortedPageable);
}

