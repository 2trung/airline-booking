package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FareRulesResponse {
    Long id;
    String ruleName;
    Long fareId;
    Long airlineId;
    Boolean isRefundable;
    Double changeFee;
    Double cancellationFee;
    Integer refundDeadlineDays;
    Integer changeDeadlineHours;
    Boolean isChangeable;
    Instant createdAt;
    Instant updatedAt;
}
