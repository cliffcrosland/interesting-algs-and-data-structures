package com.cliffcrosland.dijkstra;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

/**
 * Created by cliftoncrosland on 5/27/15.
 */
public class DijkstraShortestPath {
    public static List<GraphEdge> getShortestPathBetweenGraphNodes(GraphNode start, GraphNode finish) {
        Map<GraphNode, GraphEdgePath> shortestPathsMap = new HashMap<GraphNode, GraphEdgePath>();
        Queue<GraphNode> queue = new ArrayDeque<GraphNode>();
        queue.add(start);
        shortestPathsMap.put(start, new GraphEdgePath());
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();
            GraphEdgePath path = shortestPathsMap.get(node);
            for (GraphEdge edge : node.edges) {
                boolean shouldExplore = !shortestPathsMap.containsKey(edge.to) ||
                        path.cost + edge.value < shortestPathsMap.get(edge.to).cost;
                if (shouldExplore) {
                    GraphEdgePath nextPath = path.newPathByAppendingEdge(edge);
                    shortestPathsMap.put(edge.to, nextPath);
                    queue.add(edge.to);
                }
            }
        }
        if (shortestPathsMap.containsKey(finish)) {
            return shortestPathsMap.get(finish).path;
        }
        return null; // there is no path between start and finish.
    }

    private static class GraphEdgePath {
        List<GraphEdge> path;
        double cost;

        public GraphEdgePath() {
            this(new ArrayList<GraphEdge>(), 0.0);
        }

        private GraphEdgePath(List<GraphEdge> path, double cost) {
            this.path = path;
            this.cost = cost;
        }

        public GraphEdgePath newPathByAppendingEdge(GraphEdge edge) {
            List<GraphEdge> newPath = new ArrayList<GraphEdge>(path);
            newPath.add(edge);
            double newCost = cost + edge.value;
            return new GraphEdgePath(newPath, newCost);
        }
    }
}
