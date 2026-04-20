package com.airline.dto.response;

import com.airline.enums.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FlightScheduleResponse {

    Long id;

    Long flightId;
    String flightNumber;

    AirportResponse departureAirport;
    AirportResponse arrivalAirport;

    LocalTime departureTime;
    LocalTime arrivalTime;

    LocalDate startDate;
    LocalDate endDate;

    RecurrenceType recurrenceType;
    List<DayOfWeek> operatingDays;

    Boolean isActive;
    Long version;
}
