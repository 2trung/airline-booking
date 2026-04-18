package com.airline.repository;

import com.airline.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByBookingId(Long bookingId);

    @Query("select t from Ticket t left join fetch t.booking left join fetch t.passenger where t.booking.id = :bookingId")
    List<Ticket> findByBookingIdWithDetails(@Param("bookingId") Long bookingId);

    boolean existsByTicketNumber(String ticketNumber);
}
