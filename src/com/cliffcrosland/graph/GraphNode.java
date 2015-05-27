package com.cliffcrosland.graph;

import java.util.HashSet;
import java.util.Set;

public class GraphNode {

    public Set<GraphEdge> edges;
    public String name;

    public GraphNode(String name) {
        this(new HashSet<GraphEdge>(), name);
    }

    public GraphNode(Set<GraphEdge> edges, String name) {
        this.edges = edges;
        this.name = name;
    }

}
