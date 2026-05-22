package com.example.hackethon.relation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelationAction {
    FOLLOW(1),
    MESSAGE(2),
    COFFEE(4),
    MEAL(7),
    DRINK(10);

    private final int weight;
}
