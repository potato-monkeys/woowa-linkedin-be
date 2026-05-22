package com.example.hackethon.relation.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface ActionRequestRepository extends JpaRepository<ActionRequest, Long> {
    long countBySenderIdAndCreatedAtAfter(Long senderId, LocalDateTime startOfDay);
    boolean existsBySenderIdAndReceiverIdAndActionAndStatus(Long senderId, Long receiverId, RelationAction action, ActionRequestStatus status);
}
