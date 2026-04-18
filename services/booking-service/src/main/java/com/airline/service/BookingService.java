package com.airline.service;

import com.airline.dto.request.BookingRequest;
import com.airline.dto.response.BookingResponse;
import com.airline.enums.BookingStatus;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request, Long userId);

    BookingResponse updateBooking(Long id, BookingRequest request);

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getAllBookingsByAirline(
            Long airlineId,
            String searchQuery,
            BookingStatus status,
            Long flightInstanceId,
            String sortDirection
    );

    BookingResponse getBookingByUser(Long userId);
    BookingResponse cancelBooking(Long id);
    void deleteBooking(Long id);
}
