package com.airline.controller;

import com.airline.dto.request.BookingRequest;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.response.BookingResponse;
import com.airline.dto.response.PaymentInitiateResponse;
import com.airline.enums.BookingStatus;
import com.airline.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<PaymentInitiateResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestParam Long userId
    ) {
        PaymentInitiateResponse response = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<BookingResponse>> getAllBookingsByAirline(
            @PathVariable Long airlineId,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) Long flightInstanceId,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(
                bookingService.getAllBookingsByAirline(
                        airlineId,
                        searchQuery,
                        status,
                        flightInstanceId,
                        sortDirection
                )
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<BookingResponse> getBookingByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingByUser(userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}

