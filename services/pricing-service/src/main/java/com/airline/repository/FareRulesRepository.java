package com.airline.repository;

import com.airline.entity.FareRules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FareRulesRepository extends JpaRepository<FareRules, Long> {
    
    Optional<FareRules> findByFareId(Long fareId);
    
    List<FareRules> findByAirlineId(Long airlineId);
    
    Boolean existsByFareId(Long fareId);
}
