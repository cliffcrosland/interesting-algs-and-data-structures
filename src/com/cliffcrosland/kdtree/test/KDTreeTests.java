package com.cliffcrosland.kdtree.test;

import com.cliffcrosland.debugutils.DebugUtils;
import com.cliffcrosland.kdtree.KDTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/27/15.
 */
public class KDTreeTests {

    public static void stressTest() {
        int numPoints = 1000;
        int dimensions = 3;
        double minValue = -10;
        double maxValue = 10;
        List<double[]> points = new ArrayList<double[]>();
        for (int i = 0; i < numPoints; i++) {
            double[] point = createRandomPoint(dimensions, minValue, maxValue);
            points.add(point);
        }
        KDTree tree = new KDTree(dimensions, points);
        int k = 555;
        final double[] target = createRandomPoint(3, minValue, maxValue);

        List<double[]> kdNeighbors = tree.getKNearestNeighbors(target, k);

        points.sort(new Comparator<double[]>() {
            @Override
            public int compare(double[] a, double[] b) {
                double diff = distance(target, a) - distance(target, b);
                if (diff < 0) return -1;
                if (diff > 0) return 1;
                return 0;
            }
        });

        List<double[]> naiveNearestNeighbors = points.subList(0, Math.min(points.size(), k));

        if (kdNeighbors.size() != naiveNearestNeighbors.size()) {
            DebugUtils.printListOfPoints(Arrays.asList(target));
            DebugUtils.printListOfPoints(kdNeighbors);
            DebugUtils.printListOfPoints(naiveNearestNeighbors);
            throw new RuntimeException();
        }

        for (int i = 0; i < Math.min(k, kdNeighbors.size()); i++) {
            if (!arePointsEqual(naiveNearestNeighbors.get(i), kdNeighbors.get(i))) {
                DebugUtils.printListOfPoints(kdNeighbors);
                DebugUtils.printListOfPoints(naiveNearestNeighbors);
                throw new RuntimeException();
            }
        }
    }

    private static double[] createRandomPoint(int dimensions, double minValue, double maxValue) {
        double[] point = new double[dimensions];
        for (int i = 0; i < point.length; i++) {
            point[i] = Math.random() * (maxValue - minValue) + minValue;
        }
        return point;
    }

    private static double distance(double[] pointA, double[] pointB) {
        if (pointA.length != pointB.length) {
            throw new IllegalArgumentException("Points must have same length to get the distance btwn them.");
        }
        double sum = 0;
        for (int i = 0; i < pointA.length; i++) {
            double coordA = pointA[i];
            double coordB = pointB[i];
            sum += (coordA - coordB) * (coordA - coordB);
        }
        return Math.sqrt(sum);
    }

    private static boolean arePointsEqual(double[] pointA, double[] pointB) {
        if (pointA.length != pointB.length) return false;
        for (int i = 0; i < pointA.length; i++) {
            if (pointA[i] != pointB[i]) return false;
        }
        return true;
    }
}
