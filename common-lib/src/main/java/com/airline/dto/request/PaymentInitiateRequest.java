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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PaymentInitiateRequest {
    @NotNull(message = "Payment gateway is required")
    Long userId;

    @NotNull(message = "Booking id is required")
    Long bookingId;

    @NotNull(message = "Payment gateway is required")
    PaymentGateway paymentGateway;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    Double amount;

    String description;
}
