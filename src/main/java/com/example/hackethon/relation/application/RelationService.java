package com.example.hackethon.relation.application;

import com.example.hackethon.action.domain.ActionType;
import com.example.hackethon.relation.domain.Relation;
import com.example.hackethon.relation.domain.RelationRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;

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

    public List<Relation> findAll() {
        return relationRepository.findAll();
    }

    public List<Relation> findAllByUserId(Long userId) {
        return relationRepository.findAllByUserId(userId);
    }
}
