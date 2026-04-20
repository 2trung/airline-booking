package com.airline.dto.request;

import com.airline.embeddable.ContactInfo;
import com.airline.enums.CabinClassType;
import com.airline.enums.TripType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BookingRequest {

    @NotNull(message = "Flight ID is required")
    Long flightId;

    @NotNull(message = "Flight Instance ID is required")
    Long flightInstanceId;

    @NotNull(message = "Cabin class is required")
    CabinClassType cabinClass;

//    @NotNull(message = "Trip type is required")
    TripType tripType;

    @NotNull(message = "Fare ID is required")
    Long fareId;

    @NotNull(message = "At least one passenger is required")
    @Size(min = 1, message = "At least one passenger is required")
    List<PassengerRequest> passengers;

    ContactInfo contactInfo;

    List<Long> ancillaryIds;
    List<Long> mealIds;

    String promoCode;

    List<String> seatNumbers;
}
