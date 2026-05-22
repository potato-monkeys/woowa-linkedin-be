package com.example.hackethon.relation.ui;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.hackethon.relation.application.RelationService;
import com.example.hackethon.relation.dto.RelationActionRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RelationActionRequestController {

    private final RelationService relationService;

    @GetMapping("/received")
    public ResponseEntity<List<RelationActionRequestResponse>> getReceivedRequests(
            @AuthenticationPrincipal Long currentUserId) {
        
        List<RelationActionRequestResponse> responses = relationService.getReceivedRequests(currentUserId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id) {
        
        relationService.acceptRequest(currentUserId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long id) {
        
        relationService.rejectRequest(currentUserId, id);
        return ResponseEntity.ok().build();
    }
}


