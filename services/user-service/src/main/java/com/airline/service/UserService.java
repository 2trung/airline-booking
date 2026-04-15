package com.airline.service;

import com.airline.dto.response.UserResponse;
import com.airline.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getSelfInfo(String email);

    User getUserByEmail(String email);

    User getUserById(Long id);

    Page<User> getAllUsers(Pageable pageable);
}
