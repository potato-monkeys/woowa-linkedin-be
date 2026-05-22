package com.example.hackethon.user.dto;

import com.example.hackethon.user.domain.User;

public record UserResponse(
        Long id,
        String nickname,
        String introduction,
        String profileImageUrl,
        String recordingFileUrl
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getNickname(),
                user.getIntroduction(),
                user.getProfileImageUrl(),
                user.getRecordingFileUrl()
        );
    }
}
