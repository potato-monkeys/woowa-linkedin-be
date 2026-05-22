package com.example.hackethon.graph.dto;

import java.util.List;

public record GraphResponse(
        List<GraphNodeResponse> nodes,
        List<GraphEdgeResponse> edges
) {
}
