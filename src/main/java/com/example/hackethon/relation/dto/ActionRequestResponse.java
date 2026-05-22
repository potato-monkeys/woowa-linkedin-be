package com.example.hackethon.relation.dto;

import com.example.hackethon.relation.domain.RelationAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ActionRequestResponse {
    private Long id;
    private Long senderId;
    private RelationAction action;
    private LocalDateTime createdAt;
}
