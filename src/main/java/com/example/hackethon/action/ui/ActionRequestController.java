package com.example.hackethon.action.ui;

import com.example.hackethon.action.application.ActionRequestService;
import com.example.hackethon.action.dto.ActionRequestCreateRequest;
import com.example.hackethon.action.dto.ActionRequestResponse;
import com.example.hackethon.action.dto.ActionRequestUserRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/action-requests")
@RequiredArgsConstructor
public class ActionRequestController {

    private final ActionRequestService actionRequestService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ActionRequestCreateRequest request) {
        Long id = actionRequestService.create(request);
        return ResponseEntity.created(URI.create("/api/action-requests/" + id)).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActionRequestResponse> findOne(@PathVariable Long id) {
        return ResponseEntity.ok(actionRequestService.findOne(id));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ActionRequestResponse> accept(
            @PathVariable Long id,
            @RequestBody ActionRequestUserRequest request
    ) {
        return ResponseEntity.ok(actionRequestService.accept(id, request));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ActionRequestResponse> reject(
            @PathVariable Long id,
            @RequestBody ActionRequestUserRequest request
    ) {
        return ResponseEntity.ok(actionRequestService.reject(id, request));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ActionRequestResponse> complete(
            @PathVariable Long id,
            @RequestBody ActionRequestUserRequest request
    ) {
        return ResponseEntity.ok(actionRequestService.complete(id, request));
    }
}
