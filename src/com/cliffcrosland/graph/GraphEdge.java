package com.cliffcrosland.graph;

public class GraphEdge {

    public GraphNode from;
    public GraphNode to;
    public double value;

    public GraphEdge(GraphNode from, GraphNode to, double value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }
}
