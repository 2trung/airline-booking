package com.airline.controller;

import com.airline.service.SeatInstanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat-instances")
public class SeatInstanceController {
    private final SeatInstanceService seatInstanceService;


    @PostMapping("/calculate-price")
    public Double calculateSeatPrice(@RequestBody List<Long> seatInstanceIds) {
        return seatInstanceService.calculateSeatPrice(seatInstanceIds);
    }
}
