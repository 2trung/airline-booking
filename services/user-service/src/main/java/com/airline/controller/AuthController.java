package com.airline.controller;

import com.airline.dto.request.UserLoginRequest;
import com.airline.dto.request.UserRegisterRequest;
import com.airline.dto.response.AuthResponse;
import com.airline.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            AuthResponse errorResponse = AuthResponse.builder()
                    .title("Login Failed")
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        if (response.getToken() == null) {
            HttpStatus status = HttpStatus.BAD_REQUEST;
            if ("Email already exists".equals(response.getMessage())) {
                status = HttpStatus.CONFLICT;
            } else if ("Cannot register as SYSTEM_ADMIN".equals(response.getMessage())) {
                status = HttpStatus.FORBIDDEN;
            }
            return ResponseEntity.status(status).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
