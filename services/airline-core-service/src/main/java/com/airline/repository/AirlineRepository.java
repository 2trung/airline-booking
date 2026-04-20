package com.airline.repository;

import com.airline.entity.Airline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findFirstByOwnerId(Long ownerId);

    boolean existsByIataCodeIgnoreCase(String iataCode);

    boolean existsByIcaoCodeIgnoreCase(String icaoCode);

    boolean existsByIataCodeIgnoreCaseAndIdNot(String iataCode, Long id);

    boolean existsByIcaoCodeIgnoreCaseAndIdNot(String icaoCode, Long id);

    @Query("""
            SELECT a FROM Airline a
            WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(a.iataCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(a.icaoCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(a.alias, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(COALESCE(a.alliance, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Airline> searchByKeyword(String keyword, Pageable pageable);

    List<Airline> findAllByOrderByNameAsc();
}

