package com.cliffcrosland.kruskal;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

public class KruskalMinSpanningTree {
    // Runtime: O(E log V)
    public static Set<GraphEdge> findMinSpanningTree(Graph graph) {
        List<GraphEdge> sortedEdges = new ArrayList<GraphEdge>(graph.edges);
        // Sorting occurs in O(E log V). Why O(E log V) instead of O(E log E)? Well, E is at most V^2, and
        // log(V^2) = 2 log V = O(log V), so we can say O(E log V).
        sortedEdges.sort(new Comparator<GraphEdge>() {
            @Override
            public int compare(GraphEdge a, GraphEdge b) {
                if (a.value < b.value) return -1;
                if (a.value > b.value) return 1;
                return 0;
            }
        });
        Map<GraphNode, UnionFindTreeNode> unionFindMap = new HashMap<GraphNode, UnionFindTreeNode>();
        int label = 0;
        for (GraphNode node : graph.nodes) {
            unionFindMap.put(node, new UnionFindTreeNode(++label));
        }
        Set<GraphEdge> minSpanningTree = new HashSet<GraphEdge>();
        // O(V log V), since each node's parent pointer will be updated at most O(log V) times. Reason: a node's
        // parent pointer will only be updated if its connected component is merged with a larger one, which means
        // a node's connected component will at least double in size when its parent pointer changes, which can only
        // occur a maximum of O(log V) times.
        for (GraphEdge edge : sortedEdges) {
            // Most of the time, O(1) lookup to get root. If not O(1) lookup, at worst O(log V) lookup and internal
            // updating. But each node will have its parent updated at most O(log V) times, so we have O(V log V)
            // total work done during the entire loop.
            UnionFindTreeNode fromTreeRoot = unionFindMap.get(edge.from).getRoot();
            UnionFindTreeNode toTreeRoot = unionFindMap.get(edge.to).getRoot();
            if (fromTreeRoot.label == toTreeRoot.label) continue;
            // O(1) to merge
            UnionFindTreeNode.merge(fromTreeRoot, toTreeRoot);
            minSpanningTree.add(edge);
        }

        // O(E log V) + O(V log V) = O(E log V) since E is O(V^2)
        return minSpanningTree;
    }

    private static class UnionFindTreeNode {
        public int label;
        public int rank;
        public UnionFindTreeNode parent;

        public UnionFindTreeNode(int label) {
            this.label = label;
            this.rank = 1;
            this.parent = null;
        }

        public UnionFindTreeNode getRoot() {
            if (parent == null) {
                return this;
            }
            // To improve the run-time of future calls, connect self directly to root.
            parent = parent.getRoot();
            return parent;
        }

        public static void merge(UnionFindTreeNode rootA, UnionFindTreeNode rootB) {
            assertIsRoot(rootA);
            assertIsRoot(rootB);
            UnionFindTreeNode smaller, larger;
            if (rootA.rank < rootB.rank) {
                smaller = rootA;
                larger = rootB;
            } else {
                smaller = rootB;
                larger = rootA;
            }
            smaller.parent = larger;
            larger.rank += smaller.rank;
        }

        private static void assertIsRoot(UnionFindTreeNode root) {
            if (root.parent != null) {
                throw new IllegalArgumentException("root's parent is not null, so it is not a root");
            }
        }
    }

}
