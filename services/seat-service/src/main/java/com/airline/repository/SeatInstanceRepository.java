package com.airline.repository;

import com.airline.entity.SeatInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatInstanceRepository extends JpaRepository<SeatInstance, Long> {
}
