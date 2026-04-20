package com.airline.dto.request;

import com.airline.enums.PaymentGateway;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PaymentInitiateRequest {

    @NotNull(message = "User ID is mandatory")
    Long userId;

    @NotNull(message = "bookingId is required")
    Long bookingId;

    @NotNull(message = "Payment gateway is mandatory")
    PaymentGateway gateway;

    @NotNull(message = "Amount is mandatory")
    @Positive(message = "Amount must be positive")
    Double amount;

    String description;
}
