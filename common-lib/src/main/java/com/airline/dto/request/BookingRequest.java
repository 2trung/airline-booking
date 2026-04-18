package com.airline.dto.request;

import com.airline.embeddable.ContactInfo;
import com.airline.enums.CabinClassType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BookingRequest {

    @NotNull(message = "Flight id is required")
    Long flightId;

    @NotNull(message = "Flight instance id is required")
    Long flightInstanceId;

    @NotNull(message = "Cabin class type is required")
    CabinClassType cabinClass;

    @NotNull(message = "Fare id is required")
    Long fareId;

    @NotNull(message = "Passengers are required")
    @Size(min = 1, message = "At least one passenger is required")
    List<PassengerRequest> passengers;

    ContactInfo contactInfo;
    List<Long> ancillaryIds;
    List<Long> mealIds;

    List<String> seatNumbers;
}
