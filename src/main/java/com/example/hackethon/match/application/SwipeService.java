package com.example.hackethon.match.application;

import com.example.hackethon.match.domain.Swipe;
import com.example.hackethon.match.domain.SwipeAction;
import com.example.hackethon.match.domain.SwipeRepository;
import com.example.hackethon.relation.domain.RelationActionRequest;
import com.example.hackethon.relation.domain.RelationActionRequestRepository;
import com.example.hackethon.relation.domain.RelationAction;
import com.example.hackethon.message.application.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final RelationActionRequestRepository actionRequestRepository;
    private final MessageService messageService;

    @Transactional
    public void processSwipe(Long currentUserId, Long targetUserId, SwipeAction action, RelationAction relationAction) {
        if (swipeRepository.existsBySenderIdAndReceiverId(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("Already swiped");
        }

        Swipe swipe = new Swipe(currentUserId, targetUserId, action);
        swipeRepository.save(swipe);

        if (action == SwipeAction.PROPOSE && relationAction != null) {
            RelationActionRequest request = new RelationActionRequest(currentUserId, targetUserId, relationAction);
            actionRequestRepository.save(request);
        } else if (action == SwipeAction.PASS) {
            // Send automatic message on PASS (rejecting all actions)
            messageService.sendMessage(currentUserId, targetUserId, "이번에는 아쉽게도 인연이 닿지 않았네요. 다음에 기회가 되면 이야기 나눠요!");
        }
    }
}
