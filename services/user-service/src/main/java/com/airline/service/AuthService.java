package com.airline.service;

import com.airline.dto.request.UserRegisterRequest;
import com.airline.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String email, String password) throws Exception;

    AuthResponse register(UserRegisterRequest userRegisterRequest);

}
