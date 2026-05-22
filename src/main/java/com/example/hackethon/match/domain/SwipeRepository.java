package com.example.hackethon.match.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    boolean existsBySenderIdAndReceiverId(Long senderId, Long receiverId);
    boolean existsBySenderIdAndReceiverIdAndAction(Long senderId, Long receiverId, SwipeAction action);
    long countBySenderIdAndCreatedAtAfter(Long senderId, LocalDateTime startOfDay);
}
