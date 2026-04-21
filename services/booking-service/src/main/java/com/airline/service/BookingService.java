package com.airline.service;

import com.airline.dto.request.BookingRequest;
import com.airline.dto.response.BookingResponse;
import com.airline.dto.response.BookingStatisticsResponse;
import com.airline.dto.response.PaymentInitiateResponse;
import com.airline.enums.BookingStatus;
import com.airline.exception.PaymentException;
import com.airline.exception.ResourceNotFoundException;

import java.util.List;

public interface BookingService {

    PaymentInitiateResponse createBooking(BookingRequest request, Long userId)
            throws ResourceNotFoundException, PaymentException;

    BookingResponse updateBooking(Long id, BookingRequest request)
            throws ResourceNotFoundException;

    BookingResponse getBookingById(Long id) throws ResourceNotFoundException;



    List<BookingResponse> getBookingsByAirline(
            Long userId,
            String searchQuery,
            BookingStatus status,
            Long flightInstanceId,
            String sortDirection
    );

    List<BookingResponse> getBookingsByUser(Long userId);

    BookingResponse cancelBooking(Long id) throws ResourceNotFoundException;

    void deleteBooking(Long id) throws ResourceNotFoundException;

    boolean existsById(Long id);

    long count();

    long countByFlightId(Long flightId);

    BookingStatisticsResponse getBookingStatisticsForAirline(Long airlineId);
}
