package com.airline.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FareRulesResponse {
    Long id;
    String ruleName;
    Long fareId;
    Long airlineId;
    Boolean isRefundable;
    Boolean isChangeable;
    Double changeFee;
    Double cancellationFee;
    Integer refundableDays;
    Integer changeableHours;
    Instant createdAt;
    Instant updatedAt;
}
