package com.airline.service.impl;

import com.airline.dto.request.FareRulesRequest;
import com.airline.dto.response.FareRulesResponse;
import com.airline.entity.Fare;
import com.airline.entity.FareRules;
import com.airline.mapper.FareRulesMapper;
import com.airline.repository.FareRepository;
import com.airline.repository.FareRulesRepository;
import com.airline.service.FareRulesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FareRulesServiceImpl implements FareRulesService {

    private final FareRulesRepository fareRulesRepository;
    private final FareRepository fareRepository;
    private final FareRulesMapper fareRulesMapper;

    @Override
    @Transactional
    public FareRulesResponse createFareRules(FareRulesRequest fareRulesRequest) {
        log.info("Creating fare rules: {}", fareRulesRequest.getRuleName());

        // Validate fare exists if fareId is provided
        Fare fare = fareRepository.findById(fareRulesRequest.getFareId())
                .orElseThrow(() -> new RuntimeException("Fare not found with id: " + fareRulesRequest.getFareId()));

        // Check if fare rules already exist for this fare
        if (fareRulesRepository.existsByFareId(fareRulesRequest.getFareId())) {
            throw new RuntimeException("Fare rules already exist for fare id: " + fareRulesRequest.getFareId());
        }

        FareRules fareRules = fareRulesMapper.toEntity(fareRulesRequest, fare);
        FareRules savedFareRules = fareRulesRepository.save(fareRules);

        log.info("Fare rules created successfully with id: {}", savedFareRules.getId());
        return fareRulesMapper.toResponse(savedFareRules);

    }

    @Override
    @Transactional(readOnly = true)
    public FareRulesResponse getFareRulesById(Long fareRulesId) {
        log.info("Fetching fare rules by id: {}", fareRulesId);

        FareRules fareRules = fareRulesRepository.findById(fareRulesId)
                .orElseThrow(() -> new RuntimeException("Fare rules not found with id: " + fareRulesId));

        return fareRulesMapper.toResponse(fareRules);
    }

    @Override
    @Transactional(readOnly = true)
    public FareRulesResponse getFareRulesByFareId(Long fareId) {
        log.info("Fetching fare rules by fare id: {}", fareId);

        FareRules fareRules = fareRulesRepository.findByFareId(fareId)
                .orElseThrow(() -> new RuntimeException("Fare rules not found for fare id: " + fareId));

        return fareRulesMapper.toResponse(fareRules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FareRulesResponse> getFareRulesByAirlineId(Long airlineId) {
        log.info("Fetching fare rules by airline id: {}", airlineId);

        List<FareRules> fareRulesList = fareRulesRepository.findByAirlineId(airlineId);

        return fareRulesList.stream()
                .map(fareRulesMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FareRulesResponse updateFareRules(Long fareRulesId, FareRulesRequest fareRulesRequest) {
        log.info("Updating fare rules with id: {}", fareRulesId);

        FareRules fareRules = fareRulesRepository.findById(fareRulesId)
                .orElseThrow(() -> new RuntimeException("Fare rules not found with id: " + fareRulesId));

        // Update fare if fareId is provided and different
        if (fareRulesRequest.getFareId() != null) {
            Long currentFareId = fareRules.getFare() != null ? fareRules.getFare().getId() : null;

            if (!fareRulesRequest.getFareId().equals(currentFareId)) {
                Fare fare = fareRepository.findById(fareRulesRequest.getFareId())
                        .orElseThrow(() -> new RuntimeException("Fare not found with id: " + fareRulesRequest.getFareId()));

                // Check if another fare rules exists for the new fare
                if (fareRulesRepository.existsByFareId(fareRulesRequest.getFareId())) {
                    throw new RuntimeException("Fare rules already exist for fare id: " + fareRulesRequest.getFareId());
                }

                fareRules.setFare(fare);
            }
        }

        fareRulesMapper.updateEntity(fareRules, fareRulesRequest);
        FareRules updatedFareRules = fareRulesRepository.save(fareRules);

        log.info("Fare rules updated successfully with id: {}", updatedFareRules.getId());
        return fareRulesMapper.toResponse(updatedFareRules);
    }

    @Override
    @Transactional
    public void deleteFareRules(Long fareRulesId) {
        log.info("Deleting fare rules with id: {}", fareRulesId);

        if (!fareRulesRepository.existsById(fareRulesId)) {
            throw new RuntimeException("Fare rules not found with id: " + fareRulesId);
        }

        fareRulesRepository.deleteById(fareRulesId);
        log.info("Fare rules deleted successfully with id: {}", fareRulesId);
    }
}
