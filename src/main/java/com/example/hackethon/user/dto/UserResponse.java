package com.example.hackethon.user.dto;

import com.example.hackethon.user.domain.User;

public record UserResponse(
        Long id,
        String name,
        String introduction
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getIntroduction());
    }
}
