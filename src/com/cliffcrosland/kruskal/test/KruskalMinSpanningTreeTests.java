package com.cliffcrosland.kruskal.test;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.GraphNode;
import com.cliffcrosland.kruskal.KruskalMinSpanningTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KruskalMinSpanningTreeTests {
    private static final boolean DEBUG = true;

    public static void runTestCasesFromFile() {
        String path = "./src/com/cliffcrosland/kruskal/test/kruskal-test-cases.txt";
        System.out.println("Reading test cases from file: '" + path + "'...");
        List<KruskalTestCase> testCases = readTestCasesFromFile(path);
        System.out.println("Found " + testCases.size() + " test cases. Running...");
        for (KruskalTestCase testCase : testCases) {
            Set<GraphEdge> minSpanningTree = KruskalMinSpanningTree.findMinSpanningTree(testCase.graph);
            if (treeCost(minSpanningTree) - treeCost(testCase.solution) > 1e-4) {
                println("Test case failed!");
                println("Graph:");
                printTree(testCase.graph.edges);
                println("Kruskal min span tree:");
                println("Cost: " + treeCost(minSpanningTree));
                printTree(minSpanningTree);
                println("Expected solution:");
                println("Cost: " + treeCost(testCase.solution));
                printTree(testCase.solution);
                throw new RuntimeException("Test case failed!");
            }
        }
        System.out.println("DONE. All tests successful!");
    }

    public static void testRandomMinSpanningTree() {
        int numNodes = 6;
        int maxNumEdgesPerNode = 3;
        double maxEdgeValue = 100.0;
        Graph graph = createRandomUndirectedConnectedGraph(numNodes, maxNumEdgesPerNode, maxEdgeValue);

        println("# Kruskal min spanning tree #");
        Set<GraphEdge> minSpanningTree = KruskalMinSpanningTree.findMinSpanningTree(graph);
        println("Cost: " + treeCost(minSpanningTree));
        printTree(minSpanningTree);

        println("# Brute force min spanning tree #");
        Set<GraphEdge> bruteForceMinSpanningTree = bruteForceFindMinSpanningTree(graph);
        println("Cost: " + treeCost(bruteForceMinSpanningTree));
        printTree(bruteForceMinSpanningTree);
        println("");

        if (treeCost(minSpanningTree) - treeCost(bruteForceMinSpanningTree) > 1e-4) {
            throw new RuntimeException("Kruskal min spanning tree cost is greater than brute force spanning tree cost");
        }
    }

    private static Graph createRandomUndirectedConnectedGraph(int numNodes, int maxNumEdgesPerNode, double maxEdgeValue) {
        Set<GraphNode> allNodes = new HashSet<GraphNode>();
        for (int i = 0; i < numNodes; i++) {
            allNodes.add(new GraphNode(UUID.randomUUID().toString().substring(0, 7)));
        }

        Set<GraphEdge> allEdges;

        do {
            for (GraphNode node : allNodes) {
                node.edges = new HashSet<GraphEdge>();
            }
            allEdges = new HashSet<GraphEdge>();
            for (GraphNode node : allNodes) {
                node.edges = new HashSet<GraphEdge>();
                int numEdges = (int) (Math.random() * maxNumEdgesPerNode);
                List<GraphNode> randomNodes = pickRandomKElements(allNodes, numEdges);
                for (GraphNode randomNode : randomNodes) {
                    double edgeValue = Math.random() * maxEdgeValue;
                    GraphEdge a = new GraphEdge(node, randomNode, edgeValue);
                    GraphEdge b = new GraphEdge(randomNode, node, edgeValue);
                    node.edges.add(a);
                    randomNode.edges.add(b);
                    allEdges.addAll(Arrays.asList(a, b));
                }
            }
        } while (!isSpanningTree(allEdges, allNodes));

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

    private static Set<GraphEdge> bruteForceFindMinSpanningTree(Graph graph) {
        List<Set<GraphEdge>> trees = new ArrayList<Set<GraphEdge>>();
        recursiveBruteForceFindAllTrees(
                new HashSet<GraphEdge>(),
                new ArrayList<GraphEdge>(graph.edges),
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

    private static void recursiveBruteForceFindAllTrees(Set<GraphEdge> chosen, List<GraphEdge> pool,
                                                        int minSpanningTreeSize, List<Set<GraphEdge>> trees) {
        if (pool.isEmpty()) {
            if (chosen.size() == minSpanningTreeSize) {
                trees.add(chosen);
            }
            return;
        }
        GraphEdge edge = pool.get(0);
        Set<GraphEdge> increasedChosen = new HashSet<GraphEdge>(chosen);
        increasedChosen.add(edge);
        List<GraphEdge> reducedPool = pool.subList(1, pool.size());
        recursiveBruteForceFindAllTrees(increasedChosen, reducedPool, minSpanningTreeSize, trees);
        recursiveBruteForceFindAllTrees(chosen, reducedPool, minSpanningTreeSize, trees);
    }

    private static boolean isSpanningTree(Set<GraphEdge> tree, Set<GraphNode> nodes) {
        Set<GraphNode> treeNodes = new HashSet<GraphNode>();
        for (GraphEdge edge : tree) {
            treeNodes.add(edge.from);
            treeNodes.add(edge.to);
        }
        if (!nodes.equals(treeNodes)) return false;
        return isConnectedComponent(tree);
    }

    private static boolean isConnectedComponent(Set<GraphEdge> tree) {
        Map<GraphNode, Set<GraphNode>> neighborsMap = new HashMap<GraphNode, Set<GraphNode>>();
        for (GraphEdge edge : tree) {
            if (!neighborsMap.containsKey(edge.from)) {
                neighborsMap.put(edge.from, new HashSet<GraphNode>());
            }
            if (!neighborsMap.containsKey(edge.to)) {
                neighborsMap.put(edge.to, new HashSet<GraphNode>());
            }
            neighborsMap.get(edge.from).add(edge.to);
            neighborsMap.get(edge.to).add(edge.from);
        }

        Set<GraphNode> visited = new HashSet<GraphNode>();
        Queue<GraphNode> queue = new ArrayDeque<GraphNode>();
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

    private static double treeCost(Set<GraphEdge> tree) {
        double cost = 0.0;
        for (GraphEdge edge: tree) {
            cost += edge.value;
        }
        return cost;
    }

    private static void println(Object o) {
        if (!DEBUG) return;
        System.out.println(o);
    }

    private static void print(Object o) {
        if (!DEBUG) return;
        System.out.print(o);
    }

    private static void printTree(Set<GraphEdge> tree) {
        for (GraphEdge edge : tree) {
            print("(" + edge.from.name + " <-> " + edge.to.name + ", cost: " + edge.value + ") ");
        }
        println("");
    }

    private static List<KruskalTestCase> readTestCasesFromFile(String path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("UNABLE TO READ KRUSKAL TEST FILE");
        }

        List<KruskalTestCase> testCases = new ArrayList<KruskalTestCase>();
        KruskalTestCase latestTestCase = null;
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
                latestTestCase = new KruskalTestCase();
                latestTestCase.graph = readGraphFromString(line);
            } else {
                latestTestCase.solution = readGraphFromString(line).edges;
                testCases.add(latestTestCase);
            }
        }
        return testCases;
    }

    private static Graph readGraphFromString(String string) {
        Map<String, GraphNode> nodeMap = new HashMap<String, GraphNode>();
        Set<GraphNode> allNodes = new HashSet<GraphNode>();
        Set<GraphEdge> allEdges = new HashSet<GraphEdge>();

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

    private static void printRandomTestCaseAndSolutions() {
        for (int i = 0; i < 100; i++) {
            int numNodes = 10 + (int) (Math.random() * 20);
            int maxNumEdgesPerNode = 3 + (int) (Math.random() * (numNodes - 3));
            double maxEdgeValue = 100.0;
            Graph g = createRandomUndirectedConnectedGraph(numNodes, maxNumEdgesPerNode, maxEdgeValue);
            Set<GraphEdge> solution = KruskalMinSpanningTree.findMinSpanningTree(g);
            println("# GRAPH #");
            printTree(g.edges);
            println("# SOLUTION #");
            printTree(solution);
            println("");
        }
    }

    private static class KruskalTestCase {
        public Graph graph;
        public Set<GraphEdge> solution;

        public KruskalTestCase() { }
    }
}
