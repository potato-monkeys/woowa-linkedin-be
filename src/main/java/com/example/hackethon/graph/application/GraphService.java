package com.example.hackethon.graph.application;

import com.example.hackethon.graph.dto.GraphEdgeResponse;
import com.example.hackethon.graph.dto.GraphNodeResponse;
import com.example.hackethon.graph.dto.GraphResponse;
import com.example.hackethon.relation.domain.RelationRepository;
import com.example.hackethon.user.domain.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GraphService {

    private final UserRepository userRepository;
    private final RelationRepository relationRepository;

    public GraphResponse getGraph() {
        List<GraphNodeResponse> nodes = userRepository.findAll().stream()
                .map(GraphNodeResponse::from)
                .toList();
        List<GraphEdgeResponse> edges = relationRepository.findAll().stream()
                .map(GraphEdgeResponse::from)
                .toList();
        return new GraphResponse(nodes, edges);
    }
}
