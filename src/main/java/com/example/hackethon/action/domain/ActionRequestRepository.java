package com.example.hackethon.action.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRequestRepository extends JpaRepository<ActionRequest, Long> {

    List<ActionRequest> findAllByReceiverIdAndStatus(Long receiverId, ActionRequestStatus status);

    List<ActionRequest> findAllByRequesterIdAndStatus(Long requesterId, ActionRequestStatus status);
}
