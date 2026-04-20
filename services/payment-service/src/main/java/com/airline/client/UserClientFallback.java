package com.airline.client;

import com.airline.dto.response.UserResponse;
import com.airline.exception.UserException;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long userId) throws UserException {
        return null;
    }
}
