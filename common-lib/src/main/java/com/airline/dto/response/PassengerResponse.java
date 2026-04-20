package com.airline.dto.response;

import com.airline.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PassengerResponse {

    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    LocalDate dateOfBirth;
    Gender gender;

    String passportNumber;
    String nationality;
    String frequentFlyerNumber;

    Long primaryUserId;
    String primaryUserName;

    Boolean requiresWheelchairAssistance;
    String dietaryPreferences;
    String medicalConditions;

    Boolean isActive;
    Integer age;
    Boolean isAdult;
    String fullName;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
