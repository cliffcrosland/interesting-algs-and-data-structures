package com.cliffcrosland.dijkstra.test;

import com.cliffcrosland.dijkstra.DijkstraShortestPath;
import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;

import java.util.*;

/**
 * Created by cliftoncrosland on 5/27/15.
 */
public class DijkstraShortestPathTests {

    private static final boolean DEBUG = false;

    public static void stressTest() {
        int numNodes = 10;
        int maxNumEdgesPerNode = 10;
        double maxEdgeValue = 10.0;
        for (int i = 0; i < 100; i++) {
            testCorrectness(numNodes, maxNumEdgesPerNode, maxEdgeValue);
        }
        System.out.println("DONE. All tests successful.");
    }

    public static void testCorrectness(int numNodes, int maxNumEdgesPerNode, double maxEdgeValue) {
        Graph graph = createRandomGraph(numNodes, maxNumEdgesPerNode, maxEdgeValue);

        GraphNode start = pickRandomKElements(graph.nodes, 1).get(0);
        GraphNode finish = pickRandomKElements(graph.nodes, 1).get(0);

        println("start node name: " + start.name);
        println("finish node name: " + finish.name);

        List<GraphEdge> shortestPath = DijkstraShortestPath.getShortestPathBetweenGraphNodes(start, finish);
        println("Dijkstra's shortest path: ");
        printPath(shortestPath);
        println("Brute force shortest path: ");
        List<GraphEdge> bruteForceShortestPath = getBruteForceShortestPath(start, finish);
        printPath(bruteForceShortestPath);

        if (areConnected(start, finish)) {
            if (totalCost(shortestPath) > totalCost(bruteForceShortestPath)) {
                throw new RuntimeException("Dijkstra's shortest path has higher cost than the naive brute force shortest path!");
            }
        } else {
            println("The start and finish nodes are NOT connected");
            if (shortestPath != null) {
                throw new RuntimeException("Dijkstra's alg found a path between nodes that are not actually connected! BAD!");
            }
        }
        println("");
    }

    private static Graph createRandomGraph(int numNodes, int maxNumEdgesPerNode, double maxEdgeValue) {
        Set<GraphNode> allNodes = new HashSet<GraphNode>();
        for (int i = 0; i < numNodes; i++) {
            allNodes.add(new GraphNode(UUID.randomUUID().toString().substring(0, 7)));
        }

        Set<GraphEdge> allEdges = new HashSet<GraphEdge>();

        for (GraphNode node : allNodes) {
            int numEdges = (int) (Math.random() * maxNumEdgesPerNode);
            List<GraphNode> randomNodes = pickRandomKElements(allNodes, numEdges);
            Set<GraphEdge> edges = new HashSet<GraphEdge>();
            for (GraphNode randomNode : randomNodes) {
                edges.add(new GraphEdge(node, randomNode, Math.random() * maxEdgeValue));
            }
            node.edges = edges;
            allEdges.addAll(edges);
        }

        return new Graph(allNodes, allEdges);
    }

    private static <T> List<T> pickRandomKElements(Set<T> set, int k) {
        List<T> copy = new ArrayList<T>(set);
        shuffle(copy);
        return copy.subList(0, Math.min(k, copy.size()));
    }

    private static <T> void shuffle(List<T> list) {
        for (int i = 0; i < list.size(); i++) {
            int pick = i + (int) (Math.random() * (list.size() - i));
            T temp = list.get(i);
            list.set(i, list.get(pick));
            list.set(pick, temp);
        }
    }

    private static List<GraphEdge> getBruteForceShortestPath(GraphNode start, GraphNode finish) {
        if (start == finish) {
            return new ArrayList<GraphEdge>();
        }
        List<List<GraphEdge>> allPaths = new ArrayList<List<GraphEdge>>();
        Queue<List<GraphEdge>> pathQueue = new ArrayDeque<List<GraphEdge>>();
        for (GraphEdge edge : start.edges) {
            List<GraphEdge> path = Arrays.asList(edge);
            if (containsCycle(path)) continue;
            pathQueue.add(path);
            allPaths.add(path);
        }
        while (!pathQueue.isEmpty()) {
            List<GraphEdge> currentPath = pathQueue.poll();
            GraphNode lastNode = currentPath.get(currentPath.size() - 1).to;
            for (GraphEdge edge : lastNode.edges) {
                List<GraphEdge> newPath = new ArrayList<GraphEdge>(currentPath);
                newPath.add(edge);
                if (containsCycle(newPath)) {
                    continue;
                }
                pathQueue.add(newPath);
                allPaths.add(newPath);
            }
        }
        List<GraphEdge> bestPathSoFar = null;
        for (List<GraphEdge> path : allPaths) {
            if (path.get(path.size() - 1).to == finish) {
                if (bestPathSoFar == null || totalCost(path) < totalCost(bestPathSoFar)) {
                    bestPathSoFar = path;
                }
            }
        }
        return bestPathSoFar;
    }

    private static boolean areConnected(GraphNode start, GraphNode finish) {
        Set<GraphNode> visited = new HashSet<GraphNode>();
        Queue<GraphNode> queue = new ArrayDeque<GraphNode>();
        queue.add(start);
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();
            visited.add(node);
            for (GraphEdge edge : node.edges) {
                if (visited.contains(edge.to)) continue;
                queue.add(edge.to);
            }
        }
        return visited.contains(finish);
    }

    private static double totalCost(List<GraphEdge> edges) {
        double cost = 0.0;
        for (GraphEdge edge : edges) {
            cost += edge.value;
        }
        return cost;
    }

    private static boolean containsCycle(List<GraphEdge> edges) {
        if (edges.isEmpty()) return false;
        Set<GraphNode> nodes = new HashSet<GraphNode>();
        nodes.add(edges.get(0).from);
        for (GraphEdge edge : edges) {
            if (nodes.contains(edge.to)) {
                return true;
            }
            nodes.add(edge.to);
        }
        return false;
    }

    private static void printPath(List<GraphEdge> path) {
        if (path == null) {
            println("PATH IS NULL");
            return;
        }
        if (path.isEmpty()) {
            println("PATH IS EMPTY");
            return;
        }
        println("Path cost: " + totalCost(path));
        for (GraphEdge edge : path) {
            print(edge.from.name + " -> ");
        }
        print(path.get(path.size() - 1).to.name);
        println("");
    }

    private static void println(Object o) {
        if (!DEBUG) return;
        System.out.println(o);
    }

    private static void print(Object o) {
        if (!DEBUG) return;
        System.out.print(o);
    }
}
