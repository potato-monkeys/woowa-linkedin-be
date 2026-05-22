package com.example.hackethon.message.dto;

import lombok.Getter;

@Getter
public class MessageRequest {
    private Long receiverId;
    private String content;
}
