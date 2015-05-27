package com.cliffcrosland.kruskal;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

public class KruskalMinSpanningTree {
    public static Set<GraphEdge> findMinSpanningTree(Graph graph) {
        List<GraphEdge> sortedEdges = new ArrayList<GraphEdge>(graph.edges);
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
        for (GraphEdge edge : sortedEdges) {
            UnionFindTreeNode fromTreeRoot = unionFindMap.get(edge.from).getRoot();
            UnionFindTreeNode toTreeRoot = unionFindMap.get(edge.to).getRoot();
            if (fromTreeRoot.label == toTreeRoot.label) continue;
            UnionFindTreeNode.merge(fromTreeRoot, toTreeRoot);
            minSpanningTree.add(edge);
        }
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
