package com.example.hackethon.relation.application;

import com.example.hackethon.relation.domain.ActionRequest;
import com.example.hackethon.relation.domain.ActionRequestRepository;
import com.example.hackethon.relation.domain.ActionRequestStatus;
import com.example.hackethon.relation.domain.RelationshipEdge;
import com.example.hackethon.relation.domain.RelationshipEdgeRepository;
import com.example.hackethon.relation.dto.ActionRequestResponse;
import com.example.hackethon.action.domain.ActionType;
import com.example.hackethon.relation.domain.Relation;
import com.example.hackethon.relation.domain.RelationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelationService {

    private final ActionRequestRepository actionRequestRepository;
    private final RelationshipEdgeRepository edgeRepository;
    private final RelationRepository relationRepository;

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

    @Transactional(readOnly = true)
    public List<ActionRequestResponse> getReceivedRequests(Long currentUserId) {
        return actionRequestRepository.findByReceiverIdAndStatusOrderByCreatedAtDesc(currentUserId, ActionRequestStatus.PENDING)
                .stream()
                .map(r -> new ActionRequestResponse(r.getId(), r.getSenderId(), r.getAction(), r.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Relation applyAction(Long userId1, Long userId2, ActionType actionType, LocalDateTime now) {
        Long aId = Math.min(userId1, userId2);
        Long bId = Math.max(userId1, userId2);

        return relationRepository.findByOrderedPair(aId, bId)
                .map(relation -> {
                    relation.applyAction(actionType, now);
                    return relation;
                })
                .orElseGet(() -> relationRepository.save(Relation.create(userId1, userId2, actionType, now)));
    }

    @Transactional(readOnly = true)
    public List<Relation> findAll() {
        return relationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Relation> findAllByUserId(Long userId) {
        return relationRepository.findAllByUserId(userId);
    }
}
