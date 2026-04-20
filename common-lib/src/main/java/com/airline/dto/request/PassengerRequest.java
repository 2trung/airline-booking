package com.airline.dto.request;

import com.airline.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PassengerRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[0-9]{1,3}[0-9]{4,14}$", message = "Phone number should be in international format")
    String phone;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    Gender gender;

    String seatNumber;

    @NotNull(message = "Seat instance ID is required")
    Long seatInstanceId;

    String passportNumber;
    String nationality;
    String frequentFlyerNumber;

    Boolean requiresWheelchairAssistance = false;
    String dietaryPreferences;
    String medicalConditions;
}
