package com.example.hackethon.action.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "action_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime respondedAt;

    private LocalDateTime completedAt;

    public ActionRequest(Long requesterId, Long receiverId, ActionType actionType, LocalDateTime now) {
        validate(requesterId, receiverId, actionType);
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.actionType = actionType;
        this.status = ActionRequestStatus.PENDING;
        this.createdAt = now;
    }

    public void accept(Long receiverId, LocalDateTime now) {
        validateReceiver(receiverId);
        validateStatus(ActionRequestStatus.PENDING, "수락");
        this.status = ActionRequestStatus.ACCEPTED;
        this.respondedAt = now;
    }

    public void reject(Long receiverId, LocalDateTime now) {
        validateReceiver(receiverId);
        validateStatus(ActionRequestStatus.PENDING, "거절");
        this.status = ActionRequestStatus.REJECTED;
        this.respondedAt = now;
    }

    public void complete(Long userId, LocalDateTime now) {
        if (!userId.equals(requesterId) && !userId.equals(receiverId)) {
            throw new IllegalArgumentException("요청 당사자만 수행 완료 처리할 수 있습니다.");
        }
        validateStatus(ActionRequestStatus.ACCEPTED, "수행 완료");
        this.status = ActionRequestStatus.COMPLETED;
        this.completedAt = now;
    }

    private void validateReceiver(Long receiverId) {
        if (!this.receiverId.equals(receiverId)) {
            throw new IllegalArgumentException("요청 수신자만 응답할 수 있습니다.");
        }
    }

    private void validateStatus(ActionRequestStatus required, String operation) {
        if (this.status != required) {
            throw new IllegalStateException(
                    String.format("%s 처리는 %s 상태에서만 가능합니다. 현재 상태: %s", operation, required, this.status)
            );
        }
    }

    private static void validate(Long requesterId, Long receiverId, ActionType actionType) {
        if (requesterId == null || receiverId == null) {
            throw new IllegalArgumentException("요청자/수신자 ID는 필수입니다.");
        }
        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException("자기 자신에게 행동 요청을 보낼 수 없습니다.");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("행동 종류는 필수입니다.");
        }
    }
}
