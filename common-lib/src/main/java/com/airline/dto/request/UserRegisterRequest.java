package com.airline.dto.request;

import com.airline.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
}
