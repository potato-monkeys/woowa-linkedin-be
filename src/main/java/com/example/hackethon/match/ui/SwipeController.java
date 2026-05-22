package com.example.hackethon.match.ui;

import com.example.hackethon.match.application.SwipeService;
import com.example.hackethon.match.dto.SwipeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    public ResponseEntity<Void> processSwipe(
            @RequestHeader("X-User-Id") Long currentUserId,
            @RequestBody SwipeRequest request) {
        
        swipeService.processSwipe(currentUserId, request.getTargetUserId(), request.getAction(), request.getRelationAction());
        return ResponseEntity.ok().build();
    }
}
