package com.airline.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PassengerNotificationData {

    String firstName;
    String lastName;
    String ticketNumber;
    String seatNumber;
    String passportNumber;
    String nationality;
    String gender;
    boolean adult;
    String frequentFlyerNumber;
    boolean requiresWheelchair;
    String dietaryPreferences;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPassengerType() {
        return adult ? "Adult" : "Child";
    }
}
