package com.airline.mapper;

import com.airline.dto.response.UserResponse;
import com.airline.entity.User;

import java.util.List;

public class UserMapper {
    public static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public static List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }
}
