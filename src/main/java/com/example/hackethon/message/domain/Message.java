package com.example.hackethon.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Message(Long roomId, Long senderId, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
