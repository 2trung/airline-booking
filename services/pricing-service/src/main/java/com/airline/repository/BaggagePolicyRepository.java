package com.airline.repository;

import com.airline.entity.BaggagePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaggagePolicyRepository extends JpaRepository<BaggagePolicy, Long> {

    Optional<BaggagePolicy> findByFare_Id(Long fareId);

    List<BaggagePolicy> findByAirlineId(Long airlineId);

    boolean existsByFare_Id(Long fareId);

}
