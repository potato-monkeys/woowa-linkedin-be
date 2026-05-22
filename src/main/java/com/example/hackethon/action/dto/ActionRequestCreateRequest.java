package com.example.hackethon.action.dto;

public record ActionRequestCreateRequest(
        Long requesterId,
        Long receiverId,
        String actionType
) {
}
