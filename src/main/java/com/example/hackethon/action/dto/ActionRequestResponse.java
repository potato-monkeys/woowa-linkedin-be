package com.example.hackethon.action.dto;

import com.example.hackethon.action.domain.ActionRequest;
import com.example.hackethon.action.domain.ActionRequestStatus;
import com.example.hackethon.action.domain.ActionType;
import java.time.LocalDateTime;

public record ActionRequestResponse(
        Long id,
        Long requesterId,
        Long receiverId,
        ActionType actionType,
        ActionRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime respondedAt,
        LocalDateTime completedAt
) {
    public static ActionRequestResponse from(ActionRequest actionRequest) {
        return new ActionRequestResponse(
                actionRequest.getId(),
                actionRequest.getRequesterId(),
                actionRequest.getReceiverId(),
                actionRequest.getActionType(),
                actionRequest.getStatus(),
                actionRequest.getCreatedAt(),
                actionRequest.getRespondedAt(),
                actionRequest.getCompletedAt()
        );
    }
}
