package com.cliffcrosland.kirkpatrickseidel.test;

import com.cliffcrosland.kirkpatrickseidel.KirkpatrickSeidelMaxima;
import com.cliffcrosland.kirkpatrickseidel.Point2;

import java.util.*;

/**
 * Created by Cliff on 5/30/2015.
 */
public class KirkpatrickSeidelMaximaTest {
    public static void runStressTests() {
        int numTests = 20;
        System.out.println("Running tests on Kirkpatrick Seidel maxima alg...");
        for (int i = 0; i < numTests; i++) {
            runTest();
        }
        System.out.println("DONE. All " + numTests + " tests completed successfully!");
    }

    public static void runTest() {
        int numPoints = 30000;
        List<Point2> points = generateRandomPoints(numPoints);

        System.out.print("Computing kirkpatrick seidel... ");
        long start = System.currentTimeMillis();
        List<Point2> ksMaxima = KirkpatrickSeidelMaxima.findUndominatedMaxima(points);
        long end = System.currentTimeMillis();
        System.out.println("Took " + (end - start) + " ms.");

        System.out.print("Computing naive brute force... ");
        start = System.currentTimeMillis();
        List<Point2> naiveMaxima = findMaximaNaively(points);
        end = System.currentTimeMillis();
        System.out.println("Took " + (end - start) + " ms.");
        System.out.println();

        if (!areEqualPointLists(ksMaxima, naiveMaxima)) {
            System.out.println("KS maxima:");
            System.out.println(getStringRepresentation(ksMaxima));
            System.out.println("Naive brute-force maxima:");
            System.out.println(getStringRepresentation(naiveMaxima));
            throw new RuntimeException("Maxima lists are not the same!");
        }
    }

    private static List<Point2> generateRandomPoints(int numPoints) {
        Set<Integer> xSet = new HashSet<Integer>();
        while (xSet.size() < numPoints) {
            int rand = (int) (Math.random() * numPoints * 10);
            xSet.add(rand);
        }
        Set<Integer> ySet = new HashSet<Integer>();
        while (ySet.size() < numPoints) {
            int rand = (int) (Math.random() * numPoints * 10);
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
        return getStringRepresentation(a).equals(getStringRepresentation(b));
    }

    public static String getStringRepresentation(List<Point2> points) {
        List<String> strs = new ArrayList<String>();
        for (Point2 point : points) {
            strs.add("(" + point.x + "," + point.y + ")");
        }
        strs.sort(Comparator.naturalOrder());
        return String.join(",", strs);
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
