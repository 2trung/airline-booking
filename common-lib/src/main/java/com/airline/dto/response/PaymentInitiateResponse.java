package com.airline.dto.response;

import com.airline.enums.PaymentGateway;
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
public class PaymentInitiateResponse {
    Long paymentId;
    PaymentGateway gateway;
    Double amount;
    String transactionId;
    String description;
    String checkOutUrl;
    String message;
    Boolean success;
}
