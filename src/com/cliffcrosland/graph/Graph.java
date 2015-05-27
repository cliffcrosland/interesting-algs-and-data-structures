package com.cliffcrosland.graph;

import java.util.Set;

public class Graph {

    public Set<GraphNode> nodes;
    public Set<GraphEdge> edges;

    public Graph(Set<GraphNode> nodes, Set<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
}
