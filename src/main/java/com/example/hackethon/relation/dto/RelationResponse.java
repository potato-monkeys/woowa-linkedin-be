package com.example.hackethon.relation.dto;

import com.example.hackethon.action.domain.ActionType;
import com.example.hackethon.relation.domain.Relation;
import com.example.hackethon.relation.domain.RelationLevel;
import java.time.LocalDateTime;
import java.util.List;

public record RelationResponse(
        Long id,
        Long userAId,
        Long userBId,
        int weight,
        RelationLevel level,
        String levelDescription,
        LocalDateTime lastInteractedAt,
        List<ActionType> performedActions
) {
    public static RelationResponse from(Relation relation) {
        return new RelationResponse(
                relation.getId(),
                relation.getUserAId(),
                relation.getUserBId(),
                relation.getWeight(),
                relation.getLevel(),
                relation.getLevel().getDescription(),
                relation.getLastInteractedAt(),
                List.copyOf(relation.getPerformedActions())
        );
    }
}

