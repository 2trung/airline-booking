package com.airline.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class FareRulesRequest {

    @NotBlank(message = "Rule name is required")
    String ruleName;

    @NotNull(message = "Fare ID is required")
    Long fareId;

    Long airlineId;

    Boolean isRefundable;

    @PositiveOrZero(message = "Change fee must be positive or zero")
    Double changeFee;

    @PositiveOrZero(message = "Cancellation fee must be positive or zero")
    Double cancellationFee;

    @PositiveOrZero(message = "Refund deadline days must be positive or zero")
    Integer refundDeadlineDays;

    @PositiveOrZero(message = "Change deadline hours must be positive or zero")
    Integer changeDeadlineHours;

    Boolean isChangeable;
}
