package com.example.hackethon.match.dto;

import com.example.hackethon.match.domain.SwipeAction;
import com.example.hackethon.relation.domain.RelationAction;
import lombok.Getter;

@Getter
public class SwipeRequest {
    private Long targetUserId;
    private SwipeAction action;
    private RelationAction relationAction; // Only populated if action == PROPOSE
}
