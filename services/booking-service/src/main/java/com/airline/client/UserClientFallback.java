package com.airline.client;

import com.airline.dto.response.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long userId) {
        {
            log.warn("UserClient fallback triggered for userId={}", userId);
            return null;
        }
    }
}
