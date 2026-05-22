package com.example.hackethon.action.application;

import com.example.hackethon.action.domain.ActionRequest;
import com.example.hackethon.action.domain.ActionRequestRepository;
import com.example.hackethon.action.domain.ActionType;
import com.example.hackethon.action.dto.ActionRequestCreateRequest;
import com.example.hackethon.action.dto.ActionRequestResponse;
import com.example.hackethon.action.dto.ActionRequestUserRequest;
import com.example.hackethon.relation.application.RelationService;
import com.example.hackethon.user.domain.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ActionRequestService {

    private final ActionRequestRepository actionRequestRepository;
    private final RelationService relationService;
    private final UserRepository userRepository;

    @Transactional
    public Long create(ActionRequestCreateRequest request) {
        validateUserExists(request.requesterId());
        validateUserExists(request.receiverId());
        ActionRequest actionRequest = new ActionRequest(
                request.requesterId(),
                request.receiverId(),
                ActionType.valueOf(request.actionType()),
                LocalDateTime.now()
        );
        return actionRequestRepository.save(actionRequest).getId();
    }

    @Transactional
    public ActionRequestResponse accept(Long actionRequestId, ActionRequestUserRequest request) {
        ActionRequest actionRequest = findById(actionRequestId);
        actionRequest.accept(request.userId(), LocalDateTime.now());
        return ActionRequestResponse.from(actionRequest);
    }

    @Transactional
    public ActionRequestResponse reject(Long actionRequestId, ActionRequestUserRequest request) {
        ActionRequest actionRequest = findById(actionRequestId);
        actionRequest.reject(request.userId(), LocalDateTime.now());
        return ActionRequestResponse.from(actionRequest);
    }

    @Transactional
    public ActionRequestResponse complete(Long actionRequestId, ActionRequestUserRequest request) {
        ActionRequest actionRequest = findById(actionRequestId);
        LocalDateTime now = LocalDateTime.now();
        actionRequest.complete(request.userId(), now);
        relationService.applyAction(
                actionRequest.getRequesterId(),
                actionRequest.getReceiverId(),
                actionRequest.getActionType(),
                now
        );
        return ActionRequestResponse.from(actionRequest);
    }

    public ActionRequestResponse findOne(Long actionRequestId) {
        return ActionRequestResponse.from(findById(actionRequestId));
    }

    private ActionRequest findById(Long actionRequestId) {
        return actionRequestRepository.findById(actionRequestId)
                .orElseThrow(() -> new IllegalArgumentException("행동 요청을 찾을 수 없습니다. id: " + actionRequestId));
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다. id: " + userId);
        }
    }
}
