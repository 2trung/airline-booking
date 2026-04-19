package com.airline.dto.response;

import com.airline.embeddable.ContactInfo;
import com.airline.enums.BookingStatus;
import com.airline.enums.PaymentStatus;
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
public class BookingResponse {
    Long id;
    String bookingReference;

    Long userId;
    String userName;
    String userEmail;

    Long flightId;
    String flightNumber;
    String flightName;
    String departureAirport;
    String arrivalAirport;
    Instant departureTime;
    Instant arrivalTime;

    BookingStatus status;
    Instant bookingDate;
    Instant lastModified;

    Long flightInstanceId;
    Long airlineId;


    List<PassengerResponse> passengers;
    List<SeatInstanceResponse> seatInstances;
    PaymentLinkResponse payment;
    List<FlightCabinAncillaryResponse> ancillaries;
    List<FlightMealResponse> meals;
    List<TicketResponse> tickets;


    PaymentStatus paymentStatus;
    String paymentLink;

    Long fareId;
    String fareName;
    Double baseFare;
    Double fareTaxesAndFees;
    Double fareAirlineFees;

    Integer totalPassengers;
    Double totalAmount;
    String currency;

    String specialRequests;

    String flightDuration;
    Boolean isUpcoming;
    Boolean isPast;

    ContactInfo contactInfo;
}
