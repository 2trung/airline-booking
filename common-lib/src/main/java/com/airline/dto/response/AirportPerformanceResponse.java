package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AirportPerformanceResponse {
    List<AirportStatistics> topAirportsByBookings;
    List<AirportStatistics> topAirportsByRevenue;
    List<AirportStatistics> topDepartureAirports;
    List<AirportStatistics> topArrivalAirports;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirportStatistics {
        String airportCode;
        String airportName;
        String city;
        String country;
        Long totalBookings;
        Double totalRevenue;
        Double averageRevenuePerBooking;
        Long totalFlights;
        String performanceType;
    }
}
