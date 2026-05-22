package com.example.hackethon.relation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RelationshipEdgeRepository extends JpaRepository<RelationshipEdge, Long> {
    
    @Query("SELECT r FROM RelationshipEdge r WHERE r.user1Id = :user1Id AND r.user2Id = :user2Id")
    Optional<RelationshipEdge> findEdge(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
}
