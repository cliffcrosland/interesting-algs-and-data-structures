package com.cliffcrosland.graph;

import java.util.HashSet;
import java.util.Set;

public class Graph {

    public Set<GraphNode> nodes;
    public Set<GraphEdge> edges;

    public Graph(Set<GraphNode> nodes) {
        Set<GraphEdge> edges = new HashSet<GraphEdge>();
        for (GraphNode node : nodes) {
            for (GraphEdge edge : node.edges) {
                edges.add(edge);
            }
        }
        this.nodes = nodes;
        this.edges = edges;
    }

    public Graph(Set<GraphNode> nodes, Set<GraphEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

}
