package com.example.hackethon.message.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user1Id;

    @Column(nullable = false)
    private Long user2Id;

    private String lastMessageContent;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public MessageRoom(Long user1Id, Long user2Id) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastMessage(String content) {
        this.lastMessageContent = content;
        this.updatedAt = LocalDateTime.now();
    }
}
