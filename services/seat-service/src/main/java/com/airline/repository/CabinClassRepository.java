package com.airline.repository;

import com.airline.entity.CabinClass;
import com.airline.enums.CabinClassType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CabinClassRepository extends JpaRepository<CabinClass, Long> {
    List<CabinClass> findByAirCraftIdOrderByDisplayOrderAsc(Long aircraftId);

    Optional<CabinClass> findByAirCraftIdAndName(Long aircraftId, CabinClassType name);

    Boolean existsByCodeAndAirCraftId(String Code,  Long aircraftId);

    Boolean existsByCodeAndAirCraftIdAndIdNot(String Code,  Long aircraftId, Long id);
}
