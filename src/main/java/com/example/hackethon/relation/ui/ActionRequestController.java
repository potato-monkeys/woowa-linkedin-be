package com.example.hackethon.relation.ui;

import com.example.hackethon.relation.application.RelationService;
import com.example.hackethon.relation.dto.ActionRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ActionRequestController {

    private final RelationService relationService;

    @GetMapping("/received")
    public ResponseEntity<List<ActionRequestResponse>> getReceivedRequests(
            @RequestHeader("X-User-Id") Long currentUserId) {
        
        List<ActionRequestResponse> responses = relationService.getReceivedRequests(currentUserId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptRequest(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long id) {
        
        relationService.acceptRequest(currentUserId, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectRequest(
            @RequestHeader("X-User-Id") Long currentUserId,
            @PathVariable Long id) {
        
        relationService.rejectRequest(currentUserId, id);
        return ResponseEntity.ok().build();
    }
}
