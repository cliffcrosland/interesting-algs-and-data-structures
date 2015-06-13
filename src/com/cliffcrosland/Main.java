package com.cliffcrosland;

import com.cliffcrosland.kruskal.test.KruskalMinSpanningTreeTests;
import com.cliffcrosland.prim.test.PrimMinSpanningTreeTests;

public class Main {
    public static void main(String[] args) {
        PrimMinSpanningTreeTests.runTestCasesFromFile();
        for (int i = 0; i < 10; i++) {
            PrimMinSpanningTreeTests.testRandomMinSpanningTree();
        }

        KruskalMinSpanningTreeTests.runTestCasesFromFile();
        for (int i = 0; i < 10; i++) {
            KruskalMinSpanningTreeTests.testRandomMinSpanningTree();
        }
    }
}
