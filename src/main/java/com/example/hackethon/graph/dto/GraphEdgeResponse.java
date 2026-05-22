package com.example.hackethon.graph.dto;

import com.example.hackethon.relation.domain.Relation;
import com.example.hackethon.relation.domain.RelationLevel;

public record GraphEdgeResponse(
        Long id,
        Long source,
        Long target,
        int weight,
        RelationLevel level,
        String levelDescription
) {
    public static GraphEdgeResponse from(Relation relation) {
        return new GraphEdgeResponse(
                relation.getId(),
                relation.getUserAId(),
                relation.getUserBId(),
                relation.getWeight(),
                relation.getLevel(),
                relation.getLevel().getDescription()
        );
    }
}
