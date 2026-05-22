package com.example.hackethon.relation.domain;

import com.example.hackethon.action.domain.ActionType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_a_id", "user_b_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Relation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_a_id", nullable = false)
    private Long userAId;

    @Column(name = "user_b_id", nullable = false)
    private Long userBId;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RelationLevel level;

    @Column(nullable = false)
    private LocalDateTime lastInteractedAt;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "relation_actions",
            joinColumns = @JoinColumn(name = "relation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private List<ActionType> performedActions = new ArrayList<>();

    private Relation(Long userAId, Long userBId, int weight, LocalDateTime lastInteractedAt) {
        validateDifferentUsers(userAId, userBId);
        if (userAId < userBId) {
            this.userAId = userAId;
            this.userBId = userBId;
        } else {
            this.userAId = userBId;
            this.userBId = userAId;
        }
        this.weight = weight;
        this.level = RelationLevel.from(weight);
        this.lastInteractedAt = lastInteractedAt;
    }

    public static Relation create(Long userId1, Long userId2, ActionType firstAction, LocalDateTime now) {
        Relation relation = new Relation(userId1, userId2, 0, now);
        relation.applyAction(firstAction, now);
        return relation;
    }

    public void applyAction(ActionType actionType, LocalDateTime now) {
        this.weight += actionType.getWeight();
        this.level = RelationLevel.from(this.weight);
        this.lastInteractedAt = now;
        this.performedActions.add(actionType);
    }

    public boolean involves(Long userId) {
        return userAId.equals(userId) || userBId.equals(userId);
    }

    private static void validateDifferentUsers(Long userAId, Long userBId) {
        if (userAId == null || userBId == null) {
            throw new IllegalArgumentException("관계 대상 사용자 ID가 null일 수 없습니다.");
        }
        if (userAId.equals(userBId)) {
            throw new IllegalArgumentException("자기 자신과의 관계는 생성할 수 없습니다.");
        }
    }
}
