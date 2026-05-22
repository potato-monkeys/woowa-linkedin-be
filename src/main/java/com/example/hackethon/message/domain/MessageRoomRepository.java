package com.example.hackethon.message.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {
    
    @Query("SELECT m FROM MessageRoom m WHERE (m.user1Id = :userId1 AND m.user2Id = :userId2) OR (m.user1Id = :userId2 AND m.user2Id = :userId1)")
    Optional<MessageRoom> findByUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT m FROM MessageRoom m WHERE m.user1Id = :userId OR m.user2Id = :userId ORDER BY m.updatedAt DESC")
    List<MessageRoom> findRoomsByUserId(@Param("userId") Long userId);
}
