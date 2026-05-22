package com.example.hackethon.match.ui;

import com.example.hackethon.match.application.RecommendationService;
import com.example.hackethon.match.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @RequestHeader("X-User-Id") Long currentUserId) {
        
        List<RecommendationResponse> responses = recommendationService.getRecommendations(currentUserId);
        return ResponseEntity.ok(responses);
    }
}
