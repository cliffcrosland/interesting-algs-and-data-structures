package com.cliffcrosland.minmaxheap;

import java.util.ArrayList;
import java.util.List;

/*
 * A heap that can find both max value and min value in O(1) time. Can be constructed from an array of values in O(n)
 * time. `add`, `popMin`, and `popMax` operations each require O(log n) time. Uses implicit heap array using O(n) space.
 *
 * The big idea here is that a node at an even level is less than all of its descendants, whereas a node at an odd level
 * is greater than all of its descendants. The min will be located at the root. The max will be one of the root's two
 * children (or the root if the heap's size is 1, of course).
 */
public class MinMaxHeap<T extends Comparable> {
    private List<T> heap;

    public MinMaxHeap() {
        heap = new ArrayList<T>();
    }

    // O(n) - Make a heap from a list of items in linear time.
    public MinMaxHeap(List<T> items) {
        List<T> newHeap = new ArrayList<T>(items);
        makeMinMaxHeap(newHeap);
        heap = newHeap;
    }

    // O(1) - The root of the heap will be the minimum element.
    public T peekMin() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot find min. The heap is empty.");
        }
        return heap.get(0);
    }

    // O(1) - The maximum element will be the root if the heap has size 1. Otherwise, it will be one of the two
    // children of the root.
    public T peekMax() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot find max. The heap is empty.");
        }
        if (size() == 1) {
            return heap.get(0);
        }
        int maxChild = findMaximumIndex(heap, getChildIndicies(heap, 0));
        return heap.get(maxChild);
    }

    // O(log n) - We swap the root with the final leaf, remove the root from the heap, and heapify the leaf downward
    // until the min-max heap is restored.
    public T popMin() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot pop min. The heap is empty.");
        }
        T min = heap.get(0);
        swap(heap, 0, heap.size() - 1);
        heap.remove(heap.size() - 1);
        minMaxHeapifyDownward(heap, 0);
        return min;
    }

    // O(log n) - We swap the max node (either root if heap has size 1, or one of root's children otherwise) with the
    // final leaf, remove the max node from the heap, and heapify the leaf downward until the min-max heap is restored.
    public T popMax() {
        if (isEmpty()) {
            throw new RuntimeException("Cannot pop max. The heap is empty.");
        }
        int maxChild;
        if (size() == 1) {
            maxChild = 0;
        } else {
            maxChild = findMaximumIndex(heap, getChildIndicies(heap, 0));
        }
        T max = heap.get(maxChild);
        swap(heap, maxChild, heap.size() - 1);
        heap.remove(heap.size() - 1);
        minMaxHeapifyDownward(heap, maxChild);
        return max;
    }

    // O(log n) - We add the node as the final leaf, and then heapify upward until the min-max heap is restored.
    public void add(T item) {
        heap.add(item);
        minMaxHeapifyUpward(heap, heap.size() - 1);
    }

    // O(1) - look up size
    public int size() {
        return heap.size();
    }

    // O(1) - look up size, check if zero
    public boolean isEmpty() {
        return heap.size() == 0;
    }

    // === Helpers ===

    private static <T extends Comparable> void makeMinMaxHeap(List<T> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            minMaxHeapifyDownward(list, i);
        }
    }

    private static <T extends Comparable> void minMaxHeapifyDownward(List<T> list, int i) {
        // The comments describe the steps required for a min-level. Without loss of generality, flip "min/max" and
        // and "greater than/less than" to get the right comments for a max-level.
        boolean isMinLevel = isIndexOnMinLevel(i);
        int sign = isMinLevel ? 1 : -1;
        List<Integer> childAndGrandChildIndicies = getChildAndGrandChildIndicies(list, i);
        if (childAndGrandChildIndicies.isEmpty()) {
            return;
        }
        T root = list.get(i);
        int m = findExtremeIndex(list, childAndGrandChildIndicies, isMinLevel);
        if (isGrandChildIndex(i, m)) {
            // `root` must be smaller than all of its descendants. If the minimum descendant is a grandchild, and this
            // grandchild is smaller than `root`, that's a problem that we need to fix. We can fix it by swapping
            // `root` and the min grandchild. After swapping, a new problem may arise: `root` might be bigger than its
            // new parent, and the parent must be bigger than all of its descendants. We can fix this problem by
            // swapping `root` and its new parent. Finally, after all of this swapping, we may have caused a broken
            // heap down where the min grandchild used to be, so we recursively heapify downward there.
            T grandChild = list.get(m);
            if (sign * grandChild.compareTo(root) < 0) {
                swap(list, i, m);
                T parent = list.get(getParentIndex(m));
                if (sign * root.compareTo(parent) > 0) {
                    swap(list, m, getParentIndex(m));
                }
                minMaxHeapifyDownward(list, m);
            }
        } else {
            // `root` must be smaller than all of its descendants. If the minimum descendant is a child, and this child
            // is smaller than `root`, that's a problem that we need to fix. We can fix it by swapping `root` and the
            // min child. Afterward, there is no need continue heapifying: the new root is less than all of its
            // descendants, and the new child is greater than all of its descendants.
            T child = list.get(m);
            if (sign * child.compareTo(root) < 0) {
                swap(list, i, m);
            }
        }
    }

    private static <T extends Comparable> void minMaxHeapifyUpward(List<T> list, int i) {
        // The comments describe the steps required for a min-level. Without loss of generality, flip "min/max" and
        // and "greater than/less than" to get the right comments for a max-level.
        int level = getLevel(i);
        if (level == 0) {
            return;
        }
        boolean isMinLevel = isIndexOnMinLevel(i);
        int sign = isMinLevel ? 1 : -1;
        T node = list.get(i);
        int parentIndex = getParentIndex(i);
        T parent = list.get(parentIndex);
        if (sign * node.compareTo(parent) > 0) {
            // The parent of `node` needs to be greater than all of its descendants. If `node` is greater than its
            // parent, that's a problem we need to fix. We can fix it by swapping `node` and its parent. The only place
            // where there might still be a heap problem is where the parent used to be, so we recursively heapify
            // upward there.
            swap(list, i, parentIndex);
            minMaxHeapifyUpward(list, parentIndex);
            return;
        }
        boolean hasGrandParent = (level > 1);
        if (!hasGrandParent) {
            return;
        }
        int grandParentIndex = getParentIndex(getParentIndex(i));
        T grandParent = list.get(grandParentIndex);
        if (sign * node.compareTo(grandParent) < 0) {
            // The grandparent of `node` needs to be less than all of its descendants. If `node` is less than its
            // grandparent, that's a problem we need to fix. We can fix it by swapping `node` and its grandparent. The
            // only place where there might still be a heap problem is where the grandparent used to be, so we
            // recursively heapify upward there.
            swap(list, i, grandParentIndex);
            minMaxHeapifyUpward(list, grandParentIndex);
        }
    }

    private static int getLevel(int i) {
        return (int) logBase2(i + 1);
    }

    private static double logBase2(double i) {
        return Math.log(i) / Math.log(2);
    }

    private static boolean isIndexOnMinLevel(int i) {
        return getLevel(i) % 2 == 0;
    }

    private static <T extends Comparable> void swap(List<T> list, int a, int b) {
        T temp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, temp);
    }

    private static <T extends Comparable> List<Integer> getChildAndGrandChildIndicies(List<T> list, int root) {
        List<Integer> ret = new ArrayList<Integer>();
        List<Integer> children = getChildIndicies(list, root);
        ret.addAll(children);
        for (int child : children) {
            ret.addAll(getChildIndicies(list, child));
        }
        return ret;
    }

    private static int getParentIndex(int child) {
        return (child - 1) / 2;
    }

    private static boolean isGrandChildIndex(int root, int grandChild) {
        return 4 * root + 3 <= grandChild && grandChild <= 4 * root + 6;
    }

    private static <T> List<Integer> getChildIndicies(List<T> list, int root) {
        // root has two children, 2 * root + 1 and 2 * root + 2.
        List<Integer> ret = new ArrayList<Integer>();
        int leftChild = 2 * root + 1;
        int rightChild = 2 * root + 2;
        if (leftChild < list.size()) {
            ret.add(leftChild);
        }
        if (rightChild < list.size()) {
            ret.add(rightChild);
        }
        return ret;
    }

    private static <T extends Comparable> int findMaximumIndex(List<T> list, List<Integer> indicies) {
        return findExtremeIndex(list, indicies, false);
    }

    private static <T extends Comparable> int findExtremeIndex(List<T> list, List<Integer> indicies, boolean findMinimum) {
        int sign = findMinimum ? 1 : -1;
        int extremeSoFar = indicies.get(0);
        for (Integer index : indicies) {
            if (sign * list.get(index).compareTo(list.get(extremeSoFar)) < 0) {
                extremeSoFar = index;
            }
        }
        return extremeSoFar;
    }
}
