package com.airline.dto.response;

import com.airline.enums.PaymentGateway;
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
public class PaymentInitiateResponse {

    Long paymentId;
    PaymentGateway gateway;
    String transactionId;

    Double amount;
    String currency;
    String description;

    // Frontend should redirect user to this URL for payment
    String checkoutUrl;

    String message;
    Boolean success;
}
