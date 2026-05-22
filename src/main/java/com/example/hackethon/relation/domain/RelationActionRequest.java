package com.example.hackethon.relation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "relation_action_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelationActionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationActionRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RelationActionRequest(Long senderId, Long receiverId, RelationAction action) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.action = action;
        this.status = RelationActionRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void accept() {
        this.status = RelationActionRequestStatus.ACCEPTED;
    }

    public void reject() {
        this.status = RelationActionRequestStatus.REJECTED;
    }
}

