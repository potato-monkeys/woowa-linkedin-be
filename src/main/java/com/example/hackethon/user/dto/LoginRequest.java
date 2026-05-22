package com.example.hackethon.user.dto;

public record LoginRequest(
        String nickname,
        String password
) {
}
