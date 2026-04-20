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
public class RoutePerformanceResponse {

    List<RouteStatistics> topRoutesByBookings;
    List<RouteStatistics> topRoutesByRevenue;

    @Data
    @Builder
    @FieldDefaults(level = lombok.AccessLevel.PRIVATE)
    public static class RouteStatistics {
        String route;
        String departureAirportCode;
        String departureAirportName;
        String arrivalAirportCode;
        String arrivalAirportName;
        Long totalBookings;
        Double totalRevenue;
        Double averageRevenuePerBooking;
    }
}
