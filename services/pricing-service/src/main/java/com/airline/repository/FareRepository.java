package com.airline.repository;

import com.airline.entity.Fare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FareRepository extends JpaRepository<Fare, Long> {

    Boolean existsByFlightIdAndCabinClassIdAndName(Long flightId, Long cabinClassId, String name);
    Boolean existsByFlightIdAndCabinClassIdAndNameAndIdNot(Long flightId, Long cabinClassId, String name, Long id);


    List<Fare> findByFlightIdAndCabinClassId(Long flightId, Long cabinClassId);

    @Query("SELECT f FROM Fare f WHERE f.flightId IN :flightIds AND f.cabinClassId = :cabinClassId ORDER BY f.totalPrice ASC")
    List<Fare> findLowestFaresByFlightIds(@Param("flightIds") List<Long> flightIds, @Param("cabinClassId") Long cabinClassId);

    List<Fare> findByIdIn(List<Long> fareIds);
}
