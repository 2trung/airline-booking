package com.airline.service.impl;

import com.airline.entity.SeatInstance;
import com.airline.repository.SeatInstanceRepository;
import com.airline.service.SeatInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatInstanceServiceImpl implements SeatInstanceService {

    private final SeatInstanceRepository seatInstanceRepository;

    @Override
    public Double calculateSeatPrice(List<Long> seatInstanceIds) {

        List<SeatInstance> seatInstances = seatInstanceRepository.findAllById(seatInstanceIds);
        double price = 0.0;
        for (SeatInstance seatInstance : seatInstances) {
            double seatPremium = seatInstance.getPremiumSuperCharge() != null ? seatInstance.getPremiumSuperCharge() : 0.0;
            price += seatPremium;
        }
        return price;
    }
}
