package com.example.hackethon.graph.ui;

import com.example.hackethon.graph.application.GraphService;
import com.example.hackethon.graph.dto.GraphResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graph")
@RequiredArgsConstructor
public class GraphController {

    private final GraphService graphService;

    @GetMapping
    public ResponseEntity<GraphResponse> getGraph() {
        return ResponseEntity.ok(graphService.getGraph());
    }
}
