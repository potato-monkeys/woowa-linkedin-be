package com.example.hackethon.relation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.hackethon.action.domain.ActionType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RelationTest {

    @Test
    @DisplayName("Relation은 userA.id < userB.id로 정렬되어 저장된다")
    void orderedPair() {
        LocalDateTime now = LocalDateTime.now();
        Relation relation = Relation.create(10L, 3L, ActionType.FOLLOW, now);

        assertThat(relation.getUserAId()).isEqualTo(3L);
        assertThat(relation.getUserBId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("자기 자신과의 관계는 생성할 수 없다")
    void cannotCreateSelfRelation() {
        assertThatThrownBy(() -> Relation.create(1L, 1L, ActionType.FOLLOW, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("행동을 적용하면 점수가 누적되고 레벨이 갱신된다")
    void applyAction() {
        LocalDateTime now = LocalDateTime.now();
        Relation relation = Relation.create(1L, 2L, ActionType.COFFEE, now); // +4 → AWKWARD

        assertThat(relation.getWeight()).isEqualTo(4);
        assertThat(relation.getLevel()).isEqualTo(RelationLevel.AWKWARD);

        relation.applyAction(ActionType.DRINK, now); // +10 → 14, GETTING_CLOSER

        assertThat(relation.getWeight()).isEqualTo(14);
        assertThat(relation.getLevel()).isEqualTo(RelationLevel.GETTING_CLOSER);
        assertThat(relation.getPerformedActions()).containsExactly(ActionType.COFFEE, ActionType.DRINK);
    }

    @Test
    @DisplayName("RelationLevel은 가중치 범위로 매핑된다")
    void relationLevelFromWeight() {
        assertThat(RelationLevel.from(0)).isEqualTo(RelationLevel.STRANGER);
        assertThat(RelationLevel.from(3)).isEqualTo(RelationLevel.STRANGER);
        assertThat(RelationLevel.from(4)).isEqualTo(RelationLevel.AWKWARD);
        assertThat(RelationLevel.from(9)).isEqualTo(RelationLevel.AWKWARD);
        assertThat(RelationLevel.from(10)).isEqualTo(RelationLevel.GETTING_CLOSER);
        assertThat(RelationLevel.from(19)).isEqualTo(RelationLevel.GETTING_CLOSER);
        assertThat(RelationLevel.from(20)).isEqualTo(RelationLevel.PRETTY_CLOSE);
        assertThat(RelationLevel.from(39)).isEqualTo(RelationLevel.PRETTY_CLOSE);
        assertThat(RelationLevel.from(40)).isEqualTo(RelationLevel.POTATO_ALLIANCE);
        assertThat(RelationLevel.from(999)).isEqualTo(RelationLevel.POTATO_ALLIANCE);
    }
}
