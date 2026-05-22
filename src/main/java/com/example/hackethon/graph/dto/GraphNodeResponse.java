package com.example.hackethon.graph.dto;

import com.example.hackethon.user.domain.User;

public record GraphNodeResponse(
        Long id,
        String name,
        String introduction
) {
    public static GraphNodeResponse from(User user) {
        return new GraphNodeResponse(user.getId(), user.getNickname(), user.getIntroduction());
    }
}
