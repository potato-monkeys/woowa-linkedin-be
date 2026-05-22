package com.example.hackethon.match.application;

import com.example.hackethon.match.dto.RecommendationResponse;
import com.example.hackethon.relation.domain.RelationshipEdge;
import com.example.hackethon.relation.domain.RelationshipEdgeRepository;
import com.example.hackethon.user.domain.User;
import com.example.hackethon.user.domain.UserRepository;
import com.example.hackethon.match.domain.SwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final UserRepository userRepository;
    private final SwipeRepository swipeRepository;
    private final RelationshipEdgeRepository edgeRepository;

    private static final int DAILY_LIMIT = 20;

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendations(Long currentUserId) {
        long todaySwipes = swipeRepository.countBySenderIdAndCreatedAtAfter(currentUserId, LocalDate.now().atStartOfDay());
        if (todaySwipes >= DAILY_LIMIT) {
            return List.of();
        }

        List<User> allUsers = userRepository.findAll();
        
        return allUsers.stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .filter(u -> !swipeRepository.existsBySenderIdAndReceiverId(currentUserId, u.getId()))
                .map(u -> new RecommendationResponse(u.getId(), calculateScore(currentUserId, u.getId())))
                .sorted(Comparator.comparingInt(RecommendationResponse::getScore).reversed())
                .limit(DAILY_LIMIT - todaySwipes)
                .collect(Collectors.toList());
    }

    private int calculateScore(Long currentUserId, Long targetUserId) {
        int baseScore = edgeRepository.findEdge(Math.min(currentUserId, targetUserId), Math.max(currentUserId, targetUserId))
                .map(RelationshipEdge::getWeight)
                .orElse(0);
        
        // Add random factor for demo purposes
        int randomFactor = (int)(Math.random() * 5); 
        
        return baseScore * 2 + randomFactor;
    }
}
