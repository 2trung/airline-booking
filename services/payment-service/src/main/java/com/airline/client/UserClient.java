package com.airline.client;

import com.airline.dto.response.UserResponse;
import com.airline.exception.UserException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    UserResponse getUserById(
            @PathVariable Long userId) throws UserException;
}
