package com.airline.service;

import com.airline.entity.Booking;
import com.airline.entity.Ticket;

import java.util.List;

public interface TicketService {
    List<Ticket> generateTicketsForBooking(Booking booking);
}
