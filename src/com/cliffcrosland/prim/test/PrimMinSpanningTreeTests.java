package com.cliffcrosland.prim.test;

import com.cliffcrosland.graph.Graph;
import com.cliffcrosland.graph.GraphEdge;
import com.cliffcrosland.graph.test.GraphTestUtils;
import com.cliffcrosland.prim.PrimMinSpanningTree;

import java.util.List;
import java.util.Set;

public class PrimMinSpanningTreeTests {
    private static final boolean DEBUG = true;

    public static void runTestCasesFromFile() {
        String path = "./src/com/cliffcrosland/prim/test/prim-test-cases.txt";
        System.out.println("Reading test cases from file: '" + path + "'...");
        List<GraphTestUtils.GraphTestCase> testCases = GraphTestUtils.readTestCasesFromFile(path);
        System.out.println("Found " + testCases.size() + " test cases. Running...");
        for (GraphTestUtils.GraphTestCase testCase : testCases) {
            Set<GraphEdge> minSpanningTree = PrimMinSpanningTree.findMinSpanningTree(testCase.graph);
            if (GraphTestUtils.treeCost(minSpanningTree) - GraphTestUtils.treeCost(testCase.solution) > 1e-4) {
                println("Test case failed!");
                println("Graph:");
                printTree(testCase.graph.edges);
                println("Prim min span tree:");
                println("Cost: " + GraphTestUtils.treeCost(minSpanningTree));
                printTree(minSpanningTree);
                println("Expected solution:");
                println("Cost: " + GraphTestUtils.treeCost(testCase.solution));
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
        Graph graph = GraphTestUtils.createRandomUndirectedConnectedGraph(numNodes, maxNumEdgesPerNode, maxEdgeValue);

        println("# Prim min spanning tree #");
        Set<GraphEdge> minSpanningTree = PrimMinSpanningTree.findMinSpanningTree(graph);
        println("Cost: " + GraphTestUtils.treeCost(minSpanningTree));
        printTree(minSpanningTree);

        println("# Brute force min spanning tree #");
        Set<GraphEdge> bruteForceMinSpanningTree = GraphTestUtils.bruteForceFindMinSpanningTree(graph);
        println("Cost: " + GraphTestUtils.treeCost(bruteForceMinSpanningTree));
        printTree(bruteForceMinSpanningTree);
        println("");

        if (GraphTestUtils.treeCost(minSpanningTree) - GraphTestUtils.treeCost(bruteForceMinSpanningTree) > 1e-4) {
            throw new RuntimeException("Prim min spanning tree cost is greater than brute force spanning tree cost");
        }
    }

    public static void println(Object o) {
        if (!DEBUG) return;
        System.out.println(o);
    }

    public static void print(Object o) {
        if (!DEBUG) return;
        System.out.print(o);
    }

    public static void printTree(Set<GraphEdge> tree) {
        for (GraphEdge edge : tree) {
            print("(" + edge.from.name + " <-> " + edge.to.name + ", cost: " + edge.value + ") ");
        }
        println("");
    }
}
