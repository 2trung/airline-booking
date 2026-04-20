package com.airline.service.imp;

import com.airline.client.AncillaryClient;
import com.airline.client.FlightClient;
import com.airline.client.PaymentClient;
import com.airline.client.SeatClient;
import com.airline.dto.PaymentDTO;
import com.airline.dto.request.BookingRequest;
import com.airline.dto.request.PassengerRequest;
import com.airline.dto.request.PaymentInitiateRequest;
import com.airline.dto.response.*;
import com.airline.entity.Booking;
import com.airline.entity.Passenger;
import com.airline.enums.BookingStatus;
import com.airline.enums.PaymentGateway;
import com.airline.integration.FareIntegrationService;
import com.airline.mapper.BookingMapper;
import com.airline.repository.BookingRepository;
import com.airline.service.BookingService;
import com.airline.service.PassengerService;
import com.airline.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final PassengerService passengerService;
    private final TicketService ticketService;
    private final FlightClient flightClient;
    private final FareIntegrationService fareIntegrationService;
    private final SeatClient seatClient;
    private final AncillaryClient ancillaryClient;
    private final PaymentClient paymentClient;

    @Override
    public PaymentInitiateResponse createBooking(BookingRequest request, Long userId) {
        // 1. Generate unique booking reference
        String bookingReference = generateBookingReference();

        // 2. Create passengers
        Set<Passenger> passengers = new HashSet<>();
        for (PassengerRequest passengerRequest : request.getPassengers()) {
            Passenger passenger = passengerService.createPassenger(passengerRequest, userId);
            passengers.add(passenger);
        }
        // 3. flight exist
        FlightResponse flightResponse = flightClient.getFlightById(request.getFlightId());

        // 4. Create booking with pending seats
        Booking booking = BookingMapper.toEntity(request, userId, passengers, bookingReference);
        // set airline id from flight response
        booking.setAirlineId(flightResponse.getAirline().getId());

        // 5. Set seat instance ids
        List<Long> seatInstanceIds = request.getPassengers().stream()
                .map(PassengerRequest::getSeatInstanceId)
                .toList();
        booking.setSeatInstanceIds(seatInstanceIds);

        booking = bookingRepository.save(booking);

        // Update passengers with booking reference
        for (Passenger passenger : passengers) {
            passenger.setBooking(booking);
        }

        // 6. Generate tickets for booking
        ticketService.generateTicketsForBooking(booking);

        // todo: 7. Calculate total price
        // total fare
        Double totalFare = fareIntegrationService.calculateFareTotal(request.getFareId());
        // seat
        Double seatPrice = seatClient.calculateSeatPrice(seatInstanceIds);
        // ancillaries
        Double ancillaryPrice = ancillaryClient.calculateAncillaryPrice(request.getAncillaryIds());
        // meals
        Double mealPrice = ancillaryClient.calculateMealPrice(request.getMealIds());

         Double totalPrice = totalFare + seatPrice + ancillaryPrice + mealPrice;

        PaymentInitiateRequest paymentInitiateRequest = PaymentInitiateRequest.builder()
                .bookingId(booking.getId())
                .amount(totalPrice)
                .paymentGateway(PaymentGateway.STRIPE)
                .userId(userId)
                .amount(totalPrice)
                .description("Booking Payment with reference: " + bookingReference)
                .build();
        // Init payment
         return paymentClient.initiatePayment(paymentInitiateRequest);
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        return null;
    }

    @Override
    public BookingResponse getBookingById(Long id) {
        return bookingRepository.findById(id)
                .map(this::convertToBookingResponse)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Override
    public List<BookingResponse> getAllBookingsByAirline(Long airlineId, String searchQuery, BookingStatus status, Long flightInstanceId, String sortDirection) {

        Sort.Direction direction;

        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (Exception e) {
            direction = Sort.Direction.ASC;
        }
        Sort sort = Sort.by(direction, "bookingDate");

        String statusValue = status != null ? status.name() : null;
        List<Booking> bookings = bookingRepository.findByAirlineWithFilter(airlineId, searchQuery, statusValue, flightInstanceId, sort);

        return bookings.stream()
                .map(this::convertToBookingResponse)
                .toList();
    }

    @Override
    public BookingResponse getBookingByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .findFirst()
                .map(this::convertToBookingResponse)
                .orElseThrow(() -> new RuntimeException("No booking found for user with id: " + userId));
    }

    @Override
    public BookingResponse cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (booking.getStatus() == BookingStatus.CANCELED) {
            throw new RuntimeException("Booking is already canceled");
        }
        booking.setStatus(BookingStatus.CANCELED);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToBookingResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        bookingRepository.deleteById(id);
    }

    private String generateBookingReference() {
        String reference;
        do {
            reference = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (bookingRepository.existsByBookingReference(reference));
        return reference;
    }

    private BookingResponse convertToBookingResponse(Booking booking) {
        // todo: implement later when openfeign is used

        List<FlightCabinAncillaryResponse> ancillaries = new ArrayList<>();
        List<FlightMealResponse> mealResponse = new ArrayList<>();
        PaymentDTO paymentDTO = new PaymentDTO();
        FareResponse fareResponse = new FareResponse();
        FlightResponse flightResponse = new FlightResponse();

        List<SeatInstanceResponse> seatInstanceResponses = new ArrayList<>();
        FlightInstanceResponse flightInstanceResponse = new FlightInstanceResponse();
        return BookingMapper.toResponse(
                booking,
                paymentDTO,
                fareResponse,
                flightResponse,
                flightInstanceResponse,
                ancillaries,
                mealResponse,
                seatInstanceResponses
        );
    }
}
