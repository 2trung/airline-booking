package com.airline.repository;

import com.airline.entity.Aircraft;
import com.airline.entity.Airline;
import com.airline.enums.AircraftStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Optional<Aircraft> findByCode(String code);

    boolean existsByCode(String code);

    List<Aircraft> findByStatus(AircraftStatus status);

    List<Aircraft> findByAirline(Airline airline);

    List<Aircraft> findByAirlineAndStatus(Airline airline, AircraftStatus status);

    List<Aircraft> findByAirlineAndStatusAndIsAvailable(Airline airline, AircraftStatus status, Boolean isAvailable);

    List<Aircraft> findByModelContainingIgnoreCase(String model);

    List<Aircraft> findByNextMaintenanceDateBefore(LocalDate date);

    @Query("SELECT a FROM Aircraft a " +
            "WHERE LOWER(a.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.model) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.manufacturer) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Aircraft> searchByKeyword(String keyword, Pageable pageable);
}
