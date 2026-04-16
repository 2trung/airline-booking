package com.airline.repository;

import com.airline.entity.FlightSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {

    @Query("""
            SELECT fs FROM FlightSchedule fs
            WHERE fs.flight.airlineId = :airlineId
            """)
    List<FlightSchedule> findByAirlineId(@Param("airlineId") Long airlineId);
}
