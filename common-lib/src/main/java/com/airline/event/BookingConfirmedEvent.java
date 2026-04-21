package com.airline.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BookingConfirmedEvent {

    // ── Booking ──────────────────────────────────────────────────────────────
    Long bookingId;
    String bookingReference;
    Instant confirmedAt;
    Instant bookingDate;
    String cabinClass;           // "ECONOMY", "BUSINESS", etc.
    String tripType;             // "ONE_WAY", "ROUND_TRIP"
    boolean flexibleTicket;

    // ── Contact (booking.contactInfo or first passenger fallback) ────────────
    Long userId;
    String userName;
    String contactEmail;
    String contactPhone;

    // ── Passengers ───────────────────────────────────────────────────────────
    List<PassengerNotificationData> passengers;

    // ── Flight ───────────────────────────────────────────────────────────────
    Long flightInstanceId;
    String flightNumber;
    String airlineName;
    String airlineLogo;
    String aircraftModel;

    // Departure
    String departureAirportCode;
    String departureAirportName;
    String departureCity;
    String departureCountry;
    String departureTerminal;
    String departureGate;
    Instant departureDateTime;

    // Arrival
    String arrivalAirportCode;
    String arrivalAirportName;
    String arrivalCity;
    String arrivalCountry;
    Instant arrivalDateTime;
    String flightDuration;

    // ── Payment ──────────────────────────────────────────────────────────────
    Double totalAmount;
    String currency;
    String transactionId;
    String providerPaymentId;
    String paymentGateway;
    Instant paidAt;

    // ── Fare Breakdown ────────────────────────────────────────────────────────
    String fareName;
    Double baseFare;
    Double taxesAndFees;
    Double seatFees;
    Double ancillaryFees;
    Double mealFees;

    // ── Baggage Allowance ─────────────────────────────────────────────────────
    Integer checkinBaggagePieces;
    Double checkinBaggageWeightPerPiece;
    Integer cabinBaggagePieces;
    Double cabinBaggageWeightPerPiece;

    // ── Fare Benefits / Policies ──────────────────────────────────────────────
    Boolean freeDateChange;
    Boolean partialRefund;
    Boolean fullRefund;
    Boolean priorityBoarding;
    Boolean loungeAccess;
    Boolean complimentaryMeals;

    // ── Legacy seat IDs (kept for seat-service to mark seats as BOOKED) ──────
    List<Long> seatInstanceIds;
}
