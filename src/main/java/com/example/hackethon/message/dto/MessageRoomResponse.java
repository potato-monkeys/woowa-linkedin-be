package com.example.hackethon.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageRoomResponse {
    private Long roomId;
    private Long partnerId;
    private String lastMessageContent;
    private long unreadCount;
    private LocalDateTime updatedAt;
}
