package com.cliffcrosland.prim;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;
import com.cliffcrosland.heap.MinHeap;

import java.util.HashSet;
import java.util.Set;

public class PrimMinSpanningTree {
    // Like Kruskal's, Prim's algorithm runs in O(E log E) = O(E log V). There are no cool insane algorithms discussions
    // to be had here, however, like we had with the lazy union-find data structure. But that's okay!
    //
    // The idea is to grow the MST from a single start node like a mold, always adding the smallest edge at the
    // frontier.
    //
    // We use a min-heap to keep track of edges at the frontier and pop off the minimum. The min-heap will have size
    // O(E), so inserting a new edge takes O(log E) time, and popping off the minimum takes O(log E) time. All together
    // we expect to do O(log E) operations E times, or O(E log E) = O(E log V^2) = O(E * 2 log V) = O(E log V).
    public static Set<GraphEdge> findMinSpanningTree(Graph graph) {
        Set<GraphEdge> ret = new HashSet<>();
        Set<GraphNode> spanned = new HashSet<>();
        MinHeap<CGraphEdge> edgeHeap = new MinHeap<>();
        if (graph.nodes.size() == 0) {
            throw new IllegalArgumentException("Graph cannot be empty.");
        }
        GraphNode newNode = graph.nodes.iterator().next();
        spanned.add(newNode);
        while (spanned.size() < graph.nodes.size()) {
            for (GraphEdge edge : newNode.edges) {
                edgeHeap.add(new CGraphEdge(edge));
            }
            GraphEdge min = getMinFrontierEdge(edgeHeap, spanned);
            ret.add(min);
            newNode = spanned.contains(min.from)
                    ? min.to
                    : min.from;
            spanned.add(newNode);
        }
        return ret;
    }

    private static GraphEdge getMinFrontierEdge(MinHeap<CGraphEdge> edgeHeap, Set<GraphNode> spanned) {
        GraphEdge min = edgeHeap.popMin().edge();
        while (!onFrontier(min, spanned)) {
            min = edgeHeap.popMin().edge();
        }
        return min;
    }

    private static boolean onFrontier(GraphEdge edge, Set<GraphNode> spanned) {
        return !(spanned.contains(edge.from) && spanned.contains(edge.to));
    }

    private static class CGraphEdge implements Comparable {
        private GraphEdge e;

        public CGraphEdge(GraphEdge e) {
            this.e = e;
        }

        public GraphEdge edge() {
            return e;
        }

        public double value() {
            return e.value;
        }

        @Override
        public int compareTo(Object o) {
            CGraphEdge other = (CGraphEdge) o;
            if (value() < other.value()) {
                return -1;
            } else if (value() > other.value()) {
                return 1;
            }
            return 0;
        }
    }
}
