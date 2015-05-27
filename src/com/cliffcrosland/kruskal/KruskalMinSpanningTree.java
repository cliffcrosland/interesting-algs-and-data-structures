package com.cliffcrosland.kruskal;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

/**
 * Created by cliftoncrosland on 5/27/15.
 */
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
        Map<GraphNode, Set<GraphNode>> forestMap = new HashMap<GraphNode, Set<GraphNode>>();
        for (GraphNode node : graph.nodes) {
            forestMap.put(node, new HashSet<GraphNode>(Arrays.asList(node)));
        }
        Set<GraphEdge> minSpanningTree = new HashSet<GraphEdge>();
        for (GraphEdge edge : sortedEdges) {
            Set<GraphNode> fromTree = forestMap.get(edge.from);
            Set<GraphNode> toTree = forestMap.get(edge.to);
            if (fromTree == toTree) continue;
            Set<GraphNode> unionTree = union(fromTree, toTree);
            for (GraphNode node : unionTree) {
                forestMap.put(node, unionTree);
            }
            minSpanningTree.add(edge);
        }
        return minSpanningTree;
    }

    private static Set<GraphNode> union(Set<GraphNode> a, Set<GraphNode> b) {
        Set<GraphNode> union = new HashSet<GraphNode>(a);
        union.addAll(b);
        return union;
    }
}
