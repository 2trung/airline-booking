package com.airline.mapper;

import com.airline.dto.PaymentDTO;
import com.airline.dto.request.BookingRequest;
import com.airline.dto.response.*;
import com.airline.entity.Booking;
import com.airline.entity.Passenger;
import com.airline.enums.BookingStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BookingMapper {

    public static Booking toEntity(BookingRequest bookingRequest,
                                   Long userId, Set<Passenger> passengers,
                                   String bookingReference

    ) {
        return Booking
                .builder()
                .userId(userId)
                .flightId(bookingRequest.getFlightId())
                .flightInstanceId(bookingRequest.getFlightInstanceId())
                .fareId(bookingRequest.getFareId())
                .contactInfo(bookingRequest.getContactInfo())
                .passengers(passengers)
                .cabinClass(bookingRequest.getCabinClass())
                .ancillaryIds(bookingRequest.getAncillaryIds())
                .mealIds(bookingRequest.getMealIds())
                .bookingReference(bookingReference)
                .status(BookingStatus.PENDING)
                .build();
    }


    public static BookingResponse toResponse(
            Booking booking,
            PaymentDTO paymentDTO,
            FareResponse fareResponse,
            FlightResponse flightResponse,
            FlightInstanceResponse flightInstanceResponse,
            List<FlightCabinAncillaryResponse> ancillaries,
            List<FlightMealResponse> meals,
            List<SeatInstanceResponse> seats
    ) {

        List<PassengerResponse> passengerResponses = booking.getPassengers() != null ?
                booking.getPassengers().stream().map(PassengerMapper::toResponse).toList() : null;

        List<TicketResponse> ticketResponses = booking.getTickets() != null ?
                booking.getTickets().stream().map(TicketMapper::toResponse).toList() : null;


        return BookingResponse
                .builder()
                .id(booking.getId())
                .bookingReference(booking.getBookingReference())
                .userId(booking.getUserId())
                // flight details
                .flightId(booking.getFlightId())
                .flightNumber(flightInstanceResponse != null ? flightInstanceResponse.getFlightNumber() : null)
                .flightName(Optional.ofNullable(flightInstanceResponse)
                        .map(f -> f.getDepartureAirport().getCity().getName()
                                + " - "
                                + f.getArrivalAirport().getCity().getName())
                        .orElse(null))
                .departureTime (flightInstanceResponse != null ? flightInstanceResponse.getDepartureTime() : null)
                .arrivalTime(flightInstanceResponse != null ? flightInstanceResponse.getArrivalTime() : null)
                .flightDuration(flightInstanceResponse != null ? flightInstanceResponse.getFormatDuration() : null)
                // airport details
                .departureAirport(flightResponse != null ? flightResponse.getDepartureAirport().getName() : null)
                .arrivalAirport(flightResponse != null ? flightResponse.getArrivalAirport().getName() : null)
                .status(booking.getStatus())
                .bookingDate(booking.getBookingDate())
                .lastModified(booking.getLastModified())
                .passengers(passengerResponses)
                .tickets(ticketResponses)

                .totalPassengers(booking.getPassengers() != null ? booking.getPassengers().size() : 0)
                .ancillaries(ancillaries)
                .meals(meals)
                .seatInstances(seats)
                .paymentStatus(paymentDTO != null ? paymentDTO.getStatus() : null)
                // fare details
                .fareId(booking.getFareId())
                .fareName(fareResponse != null ? fareResponse.getName() : null)
                .baseFare(fareResponse != null ? fareResponse.getBaseFare() : null)
                .fareTaxesAndFees(fareResponse != null ? fareResponse.getTaxesAndFees() : null)
                .fareAirlineFees(fareResponse != null ? fareResponse.getAirlineFees() : null)
                .totalAmount(fareResponse != null ? fareResponse.getTotalPrice() : null)

                .contactInfo(booking.getContactInfo())
                .build();
    }
}
