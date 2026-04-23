package com.airline.repository;

import com.airline.entity.Airport;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {

    Optional<Airport> findByIataCode(String iataCode);

    List<Airport> findByCityId(Long cityId);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM Airport a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.iataCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.city.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Airport> searchAirports(@Param("keyword") String keyword, Pageable pageable);
}
