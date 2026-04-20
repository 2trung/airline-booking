package com.airline.repository;

import com.airline.entity.Aircraft;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    List<Aircraft> findByAirlineOwnerId(Long ownerId);

    Page<Aircraft> findByAirlineOwnerId(Long ownerId, Pageable pageable);
}

