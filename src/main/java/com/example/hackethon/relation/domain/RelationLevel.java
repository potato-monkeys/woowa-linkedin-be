package com.example.hackethon.relation.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RelationLevel {
    STRANGER("모르는 사이", 0, 3),
    AWKWARD("어색한 사이", 4, 9),
    GETTING_CLOSER("친해지는 중", 10, 19),
    PRETTY_CLOSE("꽤 친함", 20, 39),
    POTATO_ALLIANCE("감자 동맹", 40, Integer.MAX_VALUE);

    private final String description;
    private final int minWeight;
    private final int maxWeight;

    public static RelationLevel from(int weight) {
        for (RelationLevel level : values()) {
            if (weight >= level.minWeight && weight <= level.maxWeight) {
                return level;
            }
        }
        return POTATO_ALLIANCE; // Default to highest if it exceeds
    }
}
