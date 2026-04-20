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
public class BookingStatisticsResponse {

    // Daily statistics
    Long totalBookingsToday;
    Double revenueToday;

    // Monthly statistics
    Long totalBookingsThisMonth;
    Double revenueThisMonth;

    // Daily trend data (for the last 30 days)
    List<DailyBookingData> dailyTrend;

    // Monthly revenue and bookings chart
    List<MonthlyData> monthlyData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyBookingData {
        String date; // Format: yyyy-MM-dd
        Long bookingCount;
        Double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyData {
        String month; // Format: yyyy-MM
        Long bookingCount;
        Double revenue;
    }
}
