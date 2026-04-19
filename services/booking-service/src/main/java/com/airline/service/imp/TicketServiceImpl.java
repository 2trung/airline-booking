package com.airline.service.imp;

import com.airline.entity.Booking;
import com.airline.entity.Passenger;
import com.airline.entity.Ticket;
import com.airline.enums.TicketStatus;
import com.airline.repository.TicketRepository;
import com.airline.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> generateTicketsForBooking(Booking booking) {
        List<Ticket> tickets = new ArrayList<>();

        for (Passenger passenger: booking.getPassengers()) {

            String ticketNumber = generateUniqueTicketNumber();

            Ticket ticket = Ticket
                    .builder()
                    .ticketNumber(ticketNumber)
                    .status(TicketStatus.BOOKED)
                    .issuedAt(Instant.now())
                    .booking(booking)
                    .passenger(passenger)
                    .build();
            tickets.add(ticket);
        }
        return tickets;
    }

    private String generateUniqueTicketNumber() {
        String ticketNumber;
        do {
            String datePart = Instant.now().toString().substring(0, 10);
            String randomPart = UUID.randomUUID().toString().substring(0, 8);
            ticketNumber = String.format("TICKET-%s-%s", datePart, randomPart);
        } while (ticketRepository.existsByTicketNumber(ticketNumber));

        return ticketNumber;
    }
}
