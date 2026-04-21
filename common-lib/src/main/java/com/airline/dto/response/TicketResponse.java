package com.airline.dto.response;

import com.airline.enums.TicketStatus;
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
public class TicketResponse {

    Long id;
    String ticketNumber;
    TicketStatus status;
    Instant issuedAt;

    // Booking details
    Long bookingId;
    String bookingReference;

    // Passenger details
    Long passengerId;
    String passengerFirstName;
    String passengerLastName;
    String passengerEmail;

    // Payment details
    Long paymentId;
    Double paymentAmount;
    String paymentCurrency;
}
