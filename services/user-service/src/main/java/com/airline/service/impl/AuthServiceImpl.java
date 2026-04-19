package com.airline.service.impl;

import com.airline.config.JwtProvider;
import com.airline.dto.request.UserRegisterRequest;
import com.airline.dto.response.AuthResponse;
import com.airline.entity.User;
import com.airline.enums.UserRole;
import com.airline.mapper.UserMapper;
import com.airline.repository.UserRepository;
import com.airline.service.AuthService;
import com.airline.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetails customUserDetails;

    @Override
    public AuthResponse login(String email, String password) throws Exception {

        Authentication authentication = authenticate(email, password);

        User user = userRepository.findByEmail(email);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        String jwt = jwtProvider.generateToken(authentication, user.getId());

        return AuthResponse.builder()
                .message("Welcome back " + user.getFullName() + "!")
                .title("Login Successful")
                .user(UserMapper.toUserResponse(user))
                .token(jwt)
                .build();
    }
    private Authentication authenticate(String email, String password) throws Exception {
        UserDetails userDetails = customUserDetails.loadUserByUsername(email);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new Exception("Invalid email or password");
        }
        return new UsernamePasswordAuthenticationToken(email, password, userDetails.getAuthorities());
    }

    @Override
    public AuthResponse register(UserRegisterRequest userRegisterRequest) {
        User existingUser = userRepository.findByEmail(userRegisterRequest.getEmail());
        if (existingUser != null) {
            return AuthResponse.builder()
                    .message("Email already exists")
                    .title("Registration Failed")
                    .build();
        }

        if (userRegisterRequest.getRole().equals(UserRole.ROLE_SYSTEM_ADMIN)) {
            return AuthResponse.builder()
                    .message("Cannot register as SYSTEM_ADMIN")
                    .title("Registration Failed")
                    .build();
        }


        User newUser = User.builder()
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .fullName(userRegisterRequest.getFullName())
                .phone(userRegisterRequest.getPhone())
                .role(userRegisterRequest.getRole())
                .build();

        userRepository.save(newUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userRegisterRequest.getEmail(), userRegisterRequest.getPassword());
        String jwt = jwtProvider.generateToken(authentication, newUser.getId());
        return AuthResponse.builder()
                .message("Welcome " + newUser.getFullName() + "! Your account has been created successfully.")
                .title("Registration Successful")
                .user(UserMapper.toUserResponse(newUser))
                .token(jwt)
                .build();
    }
}
