package com.example.hackethon.user.dto;

public record UpdateProfileRequest(
        String nickname,
        String introduction,
        String currentPassword,
        String newPassword
) {
}
