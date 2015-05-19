package com.cliffcrosland.kdtree;

import com.cliffcrosland.boundedpriorityqueue.BoundedPriorityQueue;
import com.cliffcrosland.quickselect.Quickselect;

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

    // O(n log n) to construct a balanced KD tree, which chooses median roots for each subtree.
    public KDTree(int numDimensions, List<double[]> points) {
        this(numDimensions);
        constructBalancedKDTree(points);
    }

    // O(log n) to add a new point
    public void add(double[] point) {
        assertSameDimensionsAsTree(point);
        root = recursiveAdd(root, point, 0);
        size++;
    }

    // O(log n) to remove a point
    public void remove(double[] point) {
        assertSameDimensionsAsTree(point);
        root = recursiveRemove(root, point, 0);
        size--;
    }

    // O(log n) to see if the tree contains a point
    public boolean contains(double[] point) {
        assertSameDimensionsAsTree(point);
        return recursiveContainsPoint(root, point, 0);
    }

    // O(log n + 2^dimensions) to find the single nearest neighbor. O(log n) steps are required to find the region
    // near where the target point is, and O(2^dimensions) steps are required to try points in the vicinity.
    public double[] getNearestNeighbor(double[] point) {
        return getKNearestNeighbors(point, 1).get(0);
    }

    // O(k * (log n + 2^dimensions)) to find the k nearest neighbors.
    public List<double[]> getKNearestNeighbors(double[] point, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be larger than 0");
        }
        BoundedPriorityQueue<double[]> neighbors = new BoundedPriorityQueue<double[]>(k);
        recursiveGetKNearestNeighbors(root, point, 0, neighbors);
        return neighbors.toListOrderedByPriority();
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

    private void recursiveGetKNearestNeighbors(KDNode root, double[] target, int depth,
                                               BoundedPriorityQueue<double[]> neighbors) {
        if (root == null) {
            return;
        }
        double distance = distance(root.point, target);
        if (!neighbors.isFull() || distance <= neighbors.peekMaxPriority()) {
            neighbors.add(root.point, distance);
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
        recursiveGetKNearestNeighbors(next, target, depth + 1, neighbors);
        // If we have found less than k nearest neighbors so far, we need to look into the other region to find more
        // neighbors. Otherwise, if the circle from the target to the k-th furthest-away neighbor we've found so far
        // crosses over into the other region, there might be a neighbor in that region that is closer than the
        // k-th furthest-away we've found so far. Hence, we need to look into the other region in that case as well.
        if (!neighbors.isFull() || Math.abs(targetCoord - rootCoord) < neighbors.peekMaxPriority()) {
            recursiveGetKNearestNeighbors(other, target, depth + 1, neighbors);
        }
    }

    private void constructBalancedKDTree(List<double[]> points) {
        root = recursiveConstructBalancedKDTree(points, 0);
        size = points.size();
    }

    // Intelligently construct balanced KD tree from a list of points. When choosing the root of a subtree, pick the
    // point that gives the median value for cutting dimension at the first depth in the tree, then recurse on the
    // children, picking medians from the left and right sub-trees for the next cutting dimension, and so on.
    //
    // The algorithm runs in O(n log n) time. At each level in the recursion, we do O(n) work, and there are O(log n)
    // levels of recursion. Here's an informal explanation:
    // - At the first level of recursion, we run quickselect in O(n) time to find the median, and then we recurse
    //   on roughly n/2 elements to the left and n/2 elements to the right. Say this quickselect call runs in c*n time.
    // - We then do quickselect on the left n/2 elements and on the right n/2 elements. Each takes c*(n/2) time.
    //   Added together, c*(n/2) + c*(n/2) = c*n. Then we recurse on four groups of roughly n/4 elements each, each
    //   of which will run quickselect in c*(n/4) time, for a combined total of 4*c*(n/4) = c*n time, and so on.
    // - At each level of the recursion, c*n work is done, and there are log(n) levels of recursion. Thus the algorithm
    //   runs in O(n log n) time.
    private KDNode recursiveConstructBalancedKDTree(List<double[]> points, int depth) {
        if (points.size() == 0) {
            return null;
        }
        final int cuttingDim = depth % numDimensions;
        points = new ArrayList<double[]>(points);
        Comparator<double[]> cuttingDimComparator = new Comparator<double[]>() {
            @Override
            public int compare(double[] pointA, double[] pointB) {
                double diff = pointA[cuttingDim] - pointB[cuttingDim];
                if (diff < 0.0) return -1;
                if (diff > 0.0) return 1;
                return 0;
            }
        };
        int medianIndex = points.size() / 2;
        int medianRank = medianIndex + 1;
        double[] medianPoint = Quickselect.selectInPlace(points, medianRank, cuttingDimComparator);
        assertSameDimensionsAsTree(medianPoint);
        KDNode root = new KDNode(medianPoint);
        List<double[]> leftPoints = new ArrayList<double[]>(points.subList(0, medianIndex));
        List<double[]> rightPoints = new ArrayList<double[]>(points.subList(medianIndex + 1, points.size()));
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
