package com.cliffcrosland.kdtree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/16/15.
 */
public class KDTree {
    private int numDimensions;
    private KDNode root;
    private int size;

    public KDTree(int numDimensions) {
        this.numDimensions = numDimensions;
        root = null;
    }

    public KDTree(int numDimensions, List<double[]> points) {
        this(numDimensions);
        constructBalancedKDTree(points);
    }

    public void add(double[] point) {
        assertSameDimensionsAsTree(point);
        root = recursiveAdd(root, point, 0);
        size++;
    }

    public void remove(double[] point) {
        assertSameDimensionsAsTree(point);
        root = recursiveRemove(root, point, 0);
        size--;
    }

    public boolean contains(double[] point) {
        assertSameDimensionsAsTree(point);
        return recursiveContainsPoint(root, point, 0);
    }

    public double[] getNearestNeighbor(double[] point) {
        NearestNeighborBestGuess bestGuess = new NearestNeighborBestGuess();
        bestGuess.distance = Double.POSITIVE_INFINITY;
        recursiveGetNearestNeighbor(root, point, 0, bestGuess);
        return bestGuess.point;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // === Helpers ===

    private void assertSameDimensionsAsTree(double[] point) {
        if (point.length != numDimensions) {
            throw new IllegalArgumentException("point must have " + numDimensions + " dimensions.");
        }
    }

    private KDNode recursiveAdd(KDNode root, double[] point, int depth) {
        if (root == null) {
            return new KDNode(point);
        }
        int cuttingDim = depth % numDimensions;
        double rootCoord = root.point[cuttingDim];
        double pointCoord = point[cuttingDim];
        if (pointCoord < rootCoord) {
            root.left = recursiveAdd(root.left, point, depth + 1);
        } else {
            root.right = recursiveAdd(root.right, point, depth + 1);
        }
        return root;
    }

    private KDNode recursiveRemove(KDNode root, double[] target, int depth) {
        if (root == null) {
            throw new IllegalArgumentException("Cannot delete. Point does not exist in tree.");
        }
        int cuttingDim = depth % numDimensions;
        double rootCoord = root.point[cuttingDim];
        double targetCoord = target[cuttingDim];
        if (arePointsEqual(root.point, target)) {
            if (root.right != null) {
                // If there is a right sub-tree, we can replace the root with the point in the right sub-tree with the
                // minimum given cutting dimension. When we do so every point in the right sub-tree will have cutting
                // dimension greater than or equal to the new root, so everything is honky dory.
                root.point = findMinPointForCuttingDimension(root.right, cuttingDim, depth + 1);
                root.right = recursiveRemove(root.right, root.point, depth + 1);
            } else if (root.left != null) {
                // If there is no right sub-tree, we might think about replacing the root with the point in the left
                // sub-tree with the maximum given cutting dimension. However, there could actually be duplicate points
                // in the left sub-tree that all have the same max cutting dimension. If we use one of them as the new
                // root, then the invariant that every point in the left sub-tree has strictly lesser cutting dimension
                // than the root would be invalidated.
                //
                // However, we can succeed! Since the root has no right sub-tree, we can move the left sub-tree to
                // be over on the right, and we can replace the root with the point that has the minimum cutting
                // dimension from the new right sub-tree. Then, everything in the right sub-tree has cutting dimension
                // greater than or equal to the root, so everything is honky dory.
                root.point = findMinPointForCuttingDimension(root.left, cuttingDim, depth + 1);
                root.right = recursiveRemove(root.left, root.point, depth + 1);
                root.left = null;
            } else {
                // We are a leaf. Can remove self.
                return null;
            }
        } else if (targetCoord < rootCoord) {
            root.left = recursiveRemove(root.left, target, depth + 1);
        } else {
            root.right = recursiveRemove(root.right, target, depth + 1);
        }
        return root;
    }

    private double[] findMinPointForCuttingDimension(KDNode root, int cuttingDim, int depth) {
        if (root == null) {
            return null;
        }
        if (depth % numDimensions == cuttingDim) {
            if (root.left == null) {
                // This is the minimum point in this sub-tree for the given cutting dimension.
                return root.point;
            }
            return findMinPointForCuttingDimension(root.left, cuttingDim, depth + 1);
        }
        double[] leftPoint = findMinPointForCuttingDimension(root.left, cuttingDim, depth + 1);
        double[] rightPoint = findMinPointForCuttingDimension(root.right, cuttingDim, depth + 1);
        return minimumPointForCuttingDimension(cuttingDim, leftPoint, rightPoint, root.point);
    }

    private static double[] minimumPointForCuttingDimension(int cuttingDim, double[]... points) {
        if (points.length == 0) {
            throw new IllegalArgumentException("Cannot ask for minimum point when no points are given.");
        }
        double[] minPointSoFar = null;
        for (int i = 0; i < points.length; i++) {
            double[] point = points[i];
            if (point == null) continue;
            if (minPointSoFar == null || point[cuttingDim] < minPointSoFar[cuttingDim]) {
                minPointSoFar = point;
            }
        }
        return minPointSoFar;
    }

    private boolean recursiveContainsPoint(KDNode root, double[] target, int depth) {
        if (root == null) {
            return false;
        }
        if (arePointsEqual(root.point, target)) {
            return true;
        }
        int cuttingDim = depth % numDimensions;
        double rootCoord = root.point[cuttingDim];
        double targetCoord = target[cuttingDim];
        if (targetCoord < rootCoord) {
            return recursiveContainsPoint(root.left, target, depth + 1);
        } else {
            return recursiveContainsPoint(root.right, target, depth + 1);
        }
    }

    private void recursiveGetNearestNeighbor(KDNode root, double[] target, int depth, NearestNeighborBestGuess bestGuess) {
        if (root == null) {
            return;
        }
        double distance = distance(root.point, target);
        if (distance < bestGuess.distance) {
            bestGuess.point = root.point;
            bestGuess.distance = distance;
        }
        int cuttingDim = depth % numDimensions;
        double rootCoord = root.point[cuttingDim];
        double targetCoord = target[cuttingDim];
        KDNode next, other;
        if (targetCoord < rootCoord) {
            next = root.left;
            other = root.right;
        } else {
            next = root.right;
            other = root.left;
        }
        // Recurse into the region that contains the target point.
        recursiveGetNearestNeighbor(next, target, depth + 1, bestGuess);
        // If the radius of the circle from the target point to the best guess point is greater than the distance
        // between the target and the root in the current cutting dimension, then a point closer than the best guess
        // might be in the other region that does not contain the target. We must check over there too.
        if (Math.abs(targetCoord - rootCoord) < bestGuess.distance) {
            recursiveGetNearestNeighbor(other, target, depth + 1, bestGuess);
        }
    }

    private void constructBalancedKDTree(List<double[]> points) {
        root = recursiveConstructBalancedKDTree(points, 0);
        size = points.size();
    }

    // Intelligently construct balanced KD tree from a list of points. When choosing the root of a subtree, pick the
    // point that gives the median value for cutting dimension at the first depth in the tree, then recurse on the
    // children, picking medians from the left and right sub-trees for the next cutting dimension, and so on.
    private KDNode recursiveConstructBalancedKDTree(List<double[]> points, int depth) {
        if (points.size() == 0) {
            return null;
        }
        final int cuttingDim = depth % numDimensions;
        points = new ArrayList<double[]>(points);
        points.sort(new Comparator<double[]>() {
            @Override
            public int compare(double[] pointA, double[] pointB) {
                double diff = pointA[cuttingDim] - pointB[cuttingDim];
                if (diff < 0.0) return -1;
                if (diff > 0.0) return 1;
                return 0;
            }
        });
        int medianIndex = points.size() / 2;
        List<double[]> leftPoints = new ArrayList<double[]>(points.subList(0, medianIndex));
        List<double[]> rightPoints = new ArrayList<double[]>(points.subList(medianIndex + 1, points.size()));
        double[] medianPoint = points.get(medianIndex);
        assertSameDimensionsAsTree(medianPoint);
        KDNode root = new KDNode(medianPoint);
        root.left = recursiveConstructBalancedKDTree(leftPoints, depth + 1);
        root.right = recursiveConstructBalancedKDTree(rightPoints, depth + 1);
        return root;
    }

    private static boolean arePointsEqual(double[] pointA, double[] pointB) {
        if (pointA.length != pointB.length) return false;
        for (int i = 0; i < pointA.length; i++) {
            if (pointA[i] != pointB[i]) return false;
        }
        return true;
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

    private class KDNode {
        public KDNode left;
        public KDNode right;
        public double[] point;
        public KDNode(double[] point) {
            this.point = point;
        }
    }

    private class NearestNeighborBestGuess {
        public double[] point = null;
        public double distance = Double.POSITIVE_INFINITY;
    }
}
