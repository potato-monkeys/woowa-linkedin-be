package com.example.hackethon.relation.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationRepository extends JpaRepository<Relation, Long> {

    @Query("SELECT r FROM Relation r WHERE r.userAId = :userAId AND r.userBId = :userBId")
    Optional<Relation> findByOrderedPair(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

    @Query("SELECT r FROM Relation r WHERE r.userAId = :userId OR r.userBId = :userId")
    List<Relation> findAllByUserId(@Param("userId") Long userId);
}

