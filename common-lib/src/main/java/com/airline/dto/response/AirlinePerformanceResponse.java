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
public class AirlinePerformanceResponse {
    List<AirlineStatistics> topAirlinesByBookings;
    List<AirlineStatistics> topAirlinesByRevenue;
    List<AirlineStatistics> topAirlinesByAverageRevenue;
    List<AirlineStatistics> topAirlinesByFlightCount;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AirlineStatistics {
        Long airlineId;
        String airlineName;
        String airlineCode;
        String country;
        String logoUrl;
        Long totalBookings;
        Double totalRevenue;
        Double averageRevenuePerBooking;
        Long totalFlights;
        Long totalRoutes;
        Double bookingGrowthRate;
        String status;
    }
}
