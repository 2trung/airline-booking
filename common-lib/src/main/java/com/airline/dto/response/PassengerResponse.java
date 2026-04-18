package com.airline.dto.response;

import com.airline.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PassengerResponse {
    Long id;

    String firstName;

    String lastName;
    String email;
    String phone;
    LocalDate dateOfBirth;
    Gender gender;

    String nationality;

    Long primaryUserId;
    String primaryUserName;

    Boolean isActive;
    Integer age;
    Boolean isAdult;
    String fullName;

    Instant createdAt;
    Instant updatedAt;
}
