package com.airline.repository;

import com.airline.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository  extends JpaRepository<Seat, Long> {
    boolean existsBySeatMapId(Long seatMapId);
}
