package com.cliffcrosland.graph.test;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;
import com.cliffcrosland.prim.PrimMinSpanningTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cliftoncrosland on 6/13/15.
 */
public class GraphTestUtils {
    private static final boolean DEBUG = true;

    public static Graph createRandomUndirectedConnectedGraph(int numNodes, int maxNumEdgesPerNode, double maxEdgeValue) {
        Set<GraphNode> nodes = new HashSet<>();
        Set<GraphEdge> edges = new HashSet<>();

        GraphNode start = new GraphNode(getRandomNodeName());

        Queue<GraphNode> queue = new ArrayDeque<>();
        nodes.add(start);
        queue.add(start);
        while (nodes.size() < numNodes) {

            GraphNode node = queue.poll();

            int numEdges = Math.min(maxNumEdgesPerNode - node.edges.size(), numNodes - nodes.size());

            for (int i = 0; i < numEdges; i++) {
                GraphNode neighbor = new GraphNode(getRandomNodeName());
                double value = randomDoubleInRange(0, maxEdgeValue);
                GraphEdge out = new GraphEdge(node, neighbor, value);
                GraphEdge in = new GraphEdge(neighbor, node, value);
                node.edges.add(out);
                neighbor.edges.add(in);
                edges.add(out);
                edges.add(in);
                nodes.add(neighbor);
                queue.add(neighbor);
            }
        }

        List<GraphNode> nodesList = new ArrayList<>(nodes);
        while (edges.size() < numNodes * maxNumEdgesPerNode) {
            GraphNode a = nodesList.get((int)(Math.random() * nodesList.size()));
            GraphNode b = nodesList.get((int)(Math.random() * nodesList.size()));
            if (a == b) continue;
            boolean alreadyExists = false;
            for (GraphEdge e : edges) {
                if (a == e.from && b == e.to) {
                    alreadyExists = true;
                    break;
                }
            }
            if (alreadyExists) continue;
            double value = randomDoubleInRange(0, maxEdgeValue);
            GraphEdge out = new GraphEdge(a, b, value);
            GraphEdge in = new GraphEdge(b, a, value);
            a.edges.add(out);
            b.edges.add(in);
            edges.add(out);
            edges.add(in);
        }

        return new Graph(nodes, edges);
    }

    private static double randomDoubleInRange(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    private static String getRandomNodeName() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    public static Set<GraphEdge> bruteForceFindMinSpanningTree(Graph graph) {
        List<Set<GraphEdge>> trees = new ArrayList<>();
        recursiveBruteForceFindAllTrees(
                new HashSet<>(),
                new ArrayList<>(graph.edges),
                graph.nodes.size() - 1,
                trees);
        Set<GraphEdge> bestTreeSoFar = null;
        for (Set<GraphEdge> tree : trees) {
            if (isSpanningTree(tree, graph.nodes)) {
                if (bestTreeSoFar == null || treeCost(tree) < treeCost(bestTreeSoFar)) {
                    bestTreeSoFar = tree;
                }
            }
        }
        return bestTreeSoFar;
    }

    public static void recursiveBruteForceFindAllTrees(Set<GraphEdge> chosen, List<GraphEdge> pool,
                                                        int minSpanningTreeSize, List<Set<GraphEdge>> trees) {
        if (pool.isEmpty()) {
            if (chosen.size() == minSpanningTreeSize) {
                trees.add(chosen);
            }
            return;
        }
        GraphEdge edge = pool.get(0);
        Set<GraphEdge> increasedChosen = new HashSet<>(chosen);
        increasedChosen.add(edge);
        List<GraphEdge> reducedPool = pool.subList(1, pool.size());
        recursiveBruteForceFindAllTrees(increasedChosen, reducedPool, minSpanningTreeSize, trees);
        recursiveBruteForceFindAllTrees(chosen, reducedPool, minSpanningTreeSize, trees);
    }

    private static boolean isSpanningTree(Set<GraphEdge> tree, Set<GraphNode> nodes) {
        Set<GraphNode> treeNodes = new HashSet<>();
        for (GraphEdge edge : tree) {
            treeNodes.add(edge.from);
            treeNodes.add(edge.to);
        }
        if (!nodes.equals(treeNodes)) return false;
        return isConnectedComponent(tree);
    }

    private static boolean isConnectedComponent(Set<GraphEdge> tree) {
        Map<GraphNode, Set<GraphNode>> neighborsMap = new HashMap<>();
        for (GraphEdge edge : tree) {
            if (!neighborsMap.containsKey(edge.from)) {
                neighborsMap.put(edge.from, new HashSet<>());
            }
            if (!neighborsMap.containsKey(edge.to)) {
                neighborsMap.put(edge.to, new HashSet<>());
            }
            neighborsMap.get(edge.from).add(edge.to);
            neighborsMap.get(edge.to).add(edge.from);
        }

        Set<GraphNode> visited = new HashSet<>();
        Queue<GraphNode> queue = new ArrayDeque<>();
        GraphNode start = tree.iterator().next().from;
        queue.add(start);
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();
            visited.add(node);
            for (GraphNode neighbor : neighborsMap.get(node)) {
                if (visited.contains(neighbor)) continue;
                queue.add(neighbor);
            }
        }

        return visited.equals(neighborsMap.keySet());
    }

    public static double treeCost(Set<GraphEdge> tree) {
        if (tree == null) return 0.0;
        double cost = 0.0;
        for (GraphEdge edge: tree) {
            cost += edge.value;
        }
        return cost;
    }

    public static List<GraphTestCase> readTestCasesFromFile(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("UNABLE TO READ Prim TEST FILE");
        }

        List<GraphTestCase> testCases = new ArrayList<GraphTestCase>();
        GraphTestCase latestTestCase = null;
        boolean willReadGraph = true;
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            if (line.contains("# GRAPH #")) {
                willReadGraph = true;
                continue;
            }
            if (line.contains("# SOLUTION #")) {
                willReadGraph = false;
                continue;
            }

            if (willReadGraph) {
                latestTestCase = new GraphTestCase();
                latestTestCase.graph = readGraphFromString(line);
            } else {
                latestTestCase.solution = readGraphFromString(line).edges;
                testCases.add(latestTestCase);
            }
        }
        return testCases;
    }

    private static Graph readGraphFromString(String string) {
        Map<String, GraphNode> nodeMap = new HashMap<>();
        Set<GraphNode> allNodes = new HashSet<>();
        Set<GraphEdge> allEdges = new HashSet<>();

        Pattern edgePattern = Pattern.compile("(\\w+ <-> \\w+, cost: \\w+\\.\\w+)");
        Pattern edgeInfoPattern = Pattern.compile("(\\w+) <-> (\\w+), cost: (\\w+\\.\\w+)");
        Matcher edgeMatcher = edgePattern.matcher(string);
        while (edgeMatcher.find()) {
            String edgeString = edgeMatcher.group();
            Matcher edgeInfoMatcher = edgeInfoPattern.matcher(edgeString);
            edgeInfoMatcher.find();
            String fromName = edgeInfoMatcher.group(1);
            String toName = edgeInfoMatcher.group(2);
            String cost = edgeInfoMatcher.group(3);
            if (!nodeMap.containsKey(fromName)) {
                nodeMap.put(fromName, new GraphNode(fromName));
            }
            if (!nodeMap.containsKey(toName)) {
                nodeMap.put(toName, new GraphNode(toName));
            }
            double value = Double.parseDouble(cost);
            GraphEdge edge = new GraphEdge(nodeMap.get(fromName), nodeMap.get(toName), value);
            nodeMap.get(fromName).edges.add(edge);
            allEdges.add(edge);
            allNodes.add(edge.from);
            allNodes.add(edge.to);
        }
        return new Graph(allNodes, allEdges);
    }

    public static class GraphTestCase {
        public Graph graph;
        public Set<GraphEdge> solution;

        public GraphTestCase() { }
    }


}
