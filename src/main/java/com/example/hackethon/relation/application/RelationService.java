package com.example.hackethon.relation.application;

import com.example.hackethon.relation.domain.ActionRequest;
import com.example.hackethon.relation.domain.ActionRequestRepository;
import com.example.hackethon.relation.domain.ActionRequestStatus;
import com.example.hackethon.relation.domain.RelationshipEdge;
import com.example.hackethon.relation.domain.RelationshipEdgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final ActionRequestRepository actionRequestRepository;
    private final RelationshipEdgeRepository edgeRepository;

    @Transactional
    public void acceptRequest(Long currentUserId, Long requestId) {
        ActionRequest request = actionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request ID"));

        if (!request.getReceiverId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are not the receiver of this request");
        }

        if (request.getStatus() != ActionRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.accept();

        RelationshipEdge edge = edgeRepository.findEdge(
                Math.min(request.getSenderId(), request.getReceiverId()), 
                Math.max(request.getSenderId(), request.getReceiverId())
        ).orElseGet(() -> edgeRepository.save(new RelationshipEdge(request.getSenderId(), request.getReceiverId())));

        edge.addWeight(request.getAction().getWeight());
    }

    @Transactional
    public void rejectRequest(Long currentUserId, Long requestId) {
        ActionRequest request = actionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid request ID"));

        if (!request.getReceiverId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are not the receiver of this request");
        }

        if (request.getStatus() != ActionRequestStatus.PENDING) {
            throw new IllegalStateException("Request is not pending");
        }

        request.reject();
    }
}
