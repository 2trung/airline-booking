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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PassengerRequest {
    @NotBlank(message = "First name is required")
    String firstName;

    @NotBlank(message = "Last name is required")
    String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number should be 10 digits")
    String phone;


    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth should be in the past or present")
    LocalDate dateOfBirth;

    @NotNull(message = "Gender is required")
    Gender gender;

    @NotNull(message = "Seat instance ID is required")
    Long seatInstanceId;

    String nationality;
}
