package com.airline.mapper;

import com.airline.dto.response.TicketResponse;
import com.airline.entity.Ticket;

public class TicketMapper {
    public static TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .status(ticket.getStatus())
                .issuedAt(ticket.getIssuedAt())
                .bookingId(ticket.getBooking() != null ? ticket.getBooking().getId() : null)
                .bookingReference(ticket.getBooking() != null ? ticket.getBooking().getBookingReference() : null)
                .passengerId(ticket.getPassenger() != null ? ticket.getPassenger().getId() : null)
                .passengerFirstName(ticket.getPassenger() != null ? ticket.getPassenger().getFirstName() : null)
                .passengerLastName(ticket.getPassenger() != null ? ticket.getPassenger().getLastName() : null)
                .passengerEmail(ticket.getPassenger() != null ? ticket.getPassenger().getEmail() : null)
//                .paymentId(ticket.getPayment() != null ? ticket.getPayment().getId() : null)
//                .paymentAmount(ticket.getPayment() != null ? ticket.getPayment().getAmount() : null)
                .build();
    }
}
