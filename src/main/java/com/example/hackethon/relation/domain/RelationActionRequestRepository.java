package com.example.hackethon.relation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RelationActionRequestRepository extends JpaRepository<RelationActionRequest, Long> {
    long countBySenderIdAndCreatedAtAfter(Long senderId, LocalDateTime startOfDay);
    boolean existsBySenderIdAndReceiverIdAndActionAndStatus(Long senderId, Long receiverId, RelationAction action, RelationActionRequestStatus status);
    List<RelationActionRequest> findByReceiverIdAndStatusOrderByCreatedAtDesc(Long receiverId, RelationActionRequestStatus status);
}

