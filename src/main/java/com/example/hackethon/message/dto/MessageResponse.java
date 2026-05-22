package com.example.hackethon.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private Long messageId;
    private Long senderId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;
}
