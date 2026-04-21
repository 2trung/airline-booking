package com.airline.controller;

import com.airline.service.PassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passengers")
@RequiredArgsConstructor
public class PassengerController {

    private final PassengerService passengerService;
}
