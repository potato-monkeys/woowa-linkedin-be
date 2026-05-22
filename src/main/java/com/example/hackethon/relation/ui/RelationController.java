package com.example.hackethon.relation.ui;

import com.example.hackethon.relation.application.RelationService;
import com.example.hackethon.relation.dto.RelationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RelationController {

    private final RelationService relationService;

    @GetMapping("/api/relations")
    public ResponseEntity<List<RelationResponse>> findAll() {
        List<RelationResponse> relations = relationService.findAll().stream()
                .map(RelationResponse::from)
                .toList();
        return ResponseEntity.ok(relations);
    }

    @GetMapping("/api/users/{id}/relations")
    public ResponseEntity<List<RelationResponse>> findAllByUserId(@PathVariable Long id) {
        List<RelationResponse> relations = relationService.findAllByUserId(id).stream()
                .map(RelationResponse::from)
                .toList();
        return ResponseEntity.ok(relations);
    }
}
