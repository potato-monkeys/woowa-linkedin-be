package com.example.hackethon.action.domain;

import lombok.Getter;

@Getter
public enum ActionType {

    FOLLOW("팔로우", 1),
    MESSAGE("쪽지 보내기", 2),
    COFFEE("커피 마시기", 4),
    MEAL("밥 먹기", 7),
    DRINK("술 마시기", 10);

    private final String description;
    private final int weight;

    ActionType(String description, int weight) {
        this.description = description;
        this.weight = weight;
    }
}
