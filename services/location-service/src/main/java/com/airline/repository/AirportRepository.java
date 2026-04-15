package com.airline.repository;

import com.airline.entity.Airport;
import com.airline.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByIataCode(String iataCode);
    boolean existsByIataCodeAndIdNot(String iataCode, Long id);

    Page<Airport> findByCity(City city, Pageable pageable);

    @Query("""
        SELECT a FROM Airport a
        WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(a.iataCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Airport> searchByKeywordIgnoreCase(String keyword, Pageable pageable);
}

