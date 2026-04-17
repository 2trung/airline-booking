package com.airline.service.impl;

import com.airline.dto.request.BaggagePolicyRequest;
import com.airline.dto.response.BaggagePolicyResponse;
import com.airline.entity.BaggagePolicy;
import com.airline.entity.Fare;
import com.airline.mapper.BaggagePolicyMapper;
import com.airline.repository.BaggagePolicyRepository;
import com.airline.repository.FareRepository;
import com.airline.service.BaggagePolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaggagePolicyServiceImpl implements BaggagePolicyService {

    private final BaggagePolicyRepository baggagePolicyRepository;
    private final FareRepository fareRepository;
    private final BaggagePolicyMapper baggagePolicyMapper;

    @Override
    @Transactional
    public BaggagePolicyResponse createBaggagePolicy(BaggagePolicyRequest baggagePolicyRequest) {
        log.info("Creating baggage policy: {}", baggagePolicyRequest.getName());

        Fare fare = fareRepository.findById(baggagePolicyRequest.getFareId())
                .orElseThrow(() -> new RuntimeException("Fare not found with id: " + baggagePolicyRequest.getFareId()));

        if (baggagePolicyRepository.existsByFare_Id(baggagePolicyRequest.getFareId())) {
            throw new RuntimeException("Baggage policy already exists for fare id: " + baggagePolicyRequest.getFareId());
        }

        BaggagePolicy baggagePolicy = baggagePolicyMapper.toEntity(baggagePolicyRequest, fare);
        BaggagePolicy saved = baggagePolicyRepository.save(baggagePolicy);

        log.info("Baggage policy created successfully with id: {}", saved.getId());
        return baggagePolicyMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BaggagePolicyResponse getBaggagePolicyById(Long baggagePolicyId) {
        log.info("Fetching baggage policy by id: {}", baggagePolicyId);

        BaggagePolicy baggagePolicy = baggagePolicyRepository.findById(baggagePolicyId)
                .orElseThrow(() -> new RuntimeException("Baggage policy not found with id: " + baggagePolicyId));

        return baggagePolicyMapper.toResponse(baggagePolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public BaggagePolicyResponse getBaggagePolicyByFareId(Long fareId) {
        log.info("Fetching baggage policy by fare id: {}", fareId);

        BaggagePolicy baggagePolicy = baggagePolicyRepository.findByFare_Id(fareId)
                .orElseThrow(() -> new RuntimeException("Baggage policy not found for fare id: " + fareId));

        return baggagePolicyMapper.toResponse(baggagePolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaggagePolicyResponse> getBaggagePoliciesByAirlineId(Long airlineId) {
        log.info("Fetching baggage policies by airline id: {}", airlineId);

        return baggagePolicyRepository.findByAirlineId(airlineId).stream()
                .map(baggagePolicyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BaggagePolicyResponse updateBaggagePolicy(Long baggagePolicyId, BaggagePolicyRequest baggagePolicyRequest) {
        log.info("Updating baggage policy with id: {}", baggagePolicyId);

        BaggagePolicy baggagePolicy = baggagePolicyRepository.findById(baggagePolicyId)
                .orElseThrow(() -> new RuntimeException("Baggage policy not found with id: " + baggagePolicyId));

        baggagePolicyMapper.updateEntity(baggagePolicy, baggagePolicyRequest);
        BaggagePolicy updated = baggagePolicyRepository.save(baggagePolicy);

        log.info("Baggage policy updated successfully with id: {}", updated.getId());
        return baggagePolicyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBaggagePolicy(Long baggagePolicyId) {
        log.info("Deleting baggage policy with id: {}", baggagePolicyId);

        if (!baggagePolicyRepository.existsById(baggagePolicyId)) {
            throw new RuntimeException("Baggage policy not found with id: " + baggagePolicyId);
        }

        baggagePolicyRepository.deleteById(baggagePolicyId);
        log.info("Baggage policy deleted successfully with id: {}", baggagePolicyId);
    }
}
