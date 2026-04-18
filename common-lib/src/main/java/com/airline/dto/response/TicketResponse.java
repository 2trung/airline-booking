package com.airline.dto.response;

import com.airline.enums.TicketStatus;
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
public class TicketResponse {
    Long id;

    String ticketNumber;
    TicketStatus status;
    Instant issuedAt;

    Long bookingId;
    String bookingReference;

    Long passengerId;
    String passengerFirstName;
    String passengerLastName;
    String passengerEmail;

    Long paymentId;
    Double paymentAmount;
}
