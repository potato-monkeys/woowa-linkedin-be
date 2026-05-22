package com.example.hackethon.relation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "relationship_edges")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelationshipEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user1Id;

    @Column(nullable = false)
    private Long user2Id;

    @Column(nullable = false)
    private int weight;

    public RelationshipEdge(Long user1Id, Long user2Id) {
        // Always store smaller ID first for consistency
        this.user1Id = Math.min(user1Id, user2Id);
        this.user2Id = Math.max(user1Id, user2Id);
        this.weight = 0;
    }

    public void addWeight(int additionalWeight) {
        this.weight += additionalWeight;
    }
}

