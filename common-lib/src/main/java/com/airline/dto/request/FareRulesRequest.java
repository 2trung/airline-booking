package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FareRulesRequest {
    @NotBlank(message = "Rule name is required")
    String ruleName;

    Long fareId;

    Long airlineId;

    Boolean isRefundable;

    Boolean isChangeable;

    @PositiveOrZero(message = "Change fee must be a positive or zero value")
    Double changeFee;

    @PositiveOrZero(message = "Cancellation fee must be a positive or zero value")
    Double cancellationFee;

    @PositiveOrZero(message = "Refundable days must be a positive or zero value")
    Integer refundableDays;

    @PositiveOrZero(message = "Changeable hours must be a positive or zero value")
    Integer changeableHours;
}
