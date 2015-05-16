package com.cliffcrosland.kdtree;

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

    public void add(double[] point) {
        assertSameDimensionsAsTree(point);
        if (root == null) {
            root = new KDNode(point);
        } else {
            recursiveAdd(root, point, 0);
        }
        size++;
    }

    public boolean contains(double[] point) {
        assertSameDimensionsAsTree(point);
        return recursiveContainsPoint(root, point, 0);
    }

    public double[] nearestNeighbor(double[] point) {
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

    // Helpers //

    private void assertSameDimensionsAsTree(double[] point) {
        if (point.length != numDimensions) {
            throw new IllegalArgumentException("point must have " + numDimensions + " dimensions.");
        }
    }

    private void recursiveAdd(KDNode root, double[] point, int depth) {
        int coordI = depth % numDimensions;
        double rootCoord = root.point[coordI];
        double pointCoord = point[coordI];
        if (pointCoord < rootCoord) {
            if (root.left == null) {
                root.left = new KDNode(point);
            } else {
                recursiveAdd(root.left, point, depth + 1);
            }
        } else {
            if (root.right == null) {
                root.right = new KDNode(point);
            } else {
                recursiveAdd(root.right, point, depth + 1);
            }
        }
    }

    private boolean recursiveContainsPoint(KDNode root, double[] target, int depth) {
        if (root == null) {
            return false;
        }
        if (arePointsEqual(root.point, target)) {
            return true;
        }
        int coordI = depth % numDimensions;
        double rootCoord = root.point[coordI];
        double targetCoord = target[coordI];
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
        int coordI = depth % numDimensions;
        double rootCoord = root.point[coordI];
        double targetCoord = target[coordI];
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
        // If the radius of the circle from the target point to the best guess point is greater than the
        // difference along the axis between the target and the root, then a point closer than the best
        // guess might be in the other region that does not contain the target. We must check over there.
        if (Math.abs(targetCoord - rootCoord) < bestGuess.distance) {
            recursiveGetNearestNeighbor(other, target, depth + 1, bestGuess);
        }
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

    private static int numChildren(KDNode node) {
        int numChildren = 0;
        if (node.left != null) numChildren++;
        if (node.right != null) numChildren++;
        return numChildren;
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
