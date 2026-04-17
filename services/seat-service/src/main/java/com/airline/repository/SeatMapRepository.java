package com.airline.repository;

import com.airline.entity.SeatMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatMapRepository extends JpaRepository<SeatMap, Long> {

    Optional<SeatMap> findByCabinClass_Id(Long cabinClassId);

    boolean existsByCabinClass_Id(Long cabinClassId);

    boolean existsByAirlineIdAndCabinClassIdAndName(Long airlineId, Long cabinClassId, String name);

    boolean existsByCabinClass_IdAndIdNot(Long cabinClassId, Long id);
}
