package com.example.hackethon.match.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "swipes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwipeAction action;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Swipe(Long senderId, Long receiverId, SwipeAction action) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }
}
