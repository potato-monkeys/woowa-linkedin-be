package com.example.hackethon.message.ui;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.hackethon.message.application.MessageService;
import com.example.hackethon.message.dto.MessageRequest;
import com.example.hackethon.message.dto.MessageResponse;
import com.example.hackethon.message.dto.MessageRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Void> sendMessage(
            @AuthenticationPrincipal Long currentUserId,
            @RequestBody MessageRequest request) {
        
        messageService.sendMessage(currentUserId, request.getReceiverId(), request.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<MessageRoomResponse>> getRooms(
            @AuthenticationPrincipal Long currentUserId) {
        
        List<MessageRoomResponse> rooms = messageService.getRooms(currentUserId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal Long currentUserId,
            @PathVariable Long userId) {
        
        List<MessageResponse> messages = messageService.getMessages(currentUserId, userId);
        return ResponseEntity.ok(messages);
    }
}

