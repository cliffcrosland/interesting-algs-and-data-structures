package com.cliffcrosland.kirkpatrickseidel.test;

import com.cliffcrosland.kirkpatrickseidel.KirkpatrickSeidelMaxima;
import com.cliffcrosland.kirkpatrickseidel.Point2;

import java.util.*;

/**
 * Created by Cliff on 5/30/2015.
 */
public class KirkpatrickSeidelMaximaTest {
    public static void runStressTests() {
        System.out.println("Running tests...");
        for (int i = 0; i < 100; i++) {
            if (i % 10 == 0) {
                System.out.println("Completed " + i + " tests...");
            }
            runTest();
        }
        System.out.println("DONE. All tests completed successfully!");
    }

    public static void runTest() {
        int numPoints = 50;
        List<Point2> points = generateRandomPoints(numPoints);
        List<Point2> ksMaxima = KirkpatrickSeidelMaxima.findUndominatedMaxima(points);
        List<Point2> naiveMaxima = findMaximaNaively(points);
        if (!areEqualPointLists(ksMaxima, naiveMaxima)) {
            throw new RuntimeException("Maxima lists are not the same!");
        }
    }

    private static List<Point2> generateRandomPoints(int numPoints) {
        Set<Integer> xSet = new HashSet<Integer>();
        while (xSet.size() < numPoints) {
            int rand = (int) (Math.random() * 1000);
            xSet.add(rand);
        }
        Set<Integer> ySet = new HashSet<Integer>();
        while (ySet.size() < numPoints) {
            int rand = (int) (Math.random() * 1000);
            ySet.add(rand);
        }
        List<Integer> xList = new ArrayList<Integer>(xSet);
        List<Integer> yList = new ArrayList<Integer>(ySet);
        List<Point2> points = new ArrayList<Point2>();
        for (int i = 0; i < xList.size(); i++) {
            points.add(new Point2(xList.get(i), yList.get(i)));
        }
        return points;
    }

    private static boolean areEqualPointLists(List<Point2> a, List<Point2> b) {
        List<String> aStrs = new ArrayList<String>();
        for (Point2 point : a) {
            aStrs.add("(" + point.x + "," + point.y + ")");
        }
        List<String> bStrs = new ArrayList<String>();
        for (Point2 point : b) {
            bStrs.add("(" + point.x + "," + point.y + ")");
        }
        aStrs.sort(Comparator.<String>naturalOrder());
        bStrs.sort(Comparator.<String>naturalOrder());
        String aStr = String.join(",", aStrs);
        String bStr = String.join(",", bStrs);
        return aStr.equals(bStr);
    }

    private static List<Point2> findMaximaNaively(List<Point2> points) {
        List<Point2> maxima = new ArrayList<Point2>();
        for (int i = 0; i < points.size(); i++) {
            Point2 candidate = points.get(i);
            boolean isMax = true;
            for (int j = 0; j < points.size(); j++) {
                if (i == j) continue;
                Point2 point = points.get(j);
                if (point.x > candidate.x && point.y > candidate.y) {
                    isMax = false;
                    break;
                }
            }
            if (isMax) {
                maxima.add(candidate);
            }
        }
        return maxima;
    }
}
