package com.cliffcrosland.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class MinHeap<T extends Comparable> {
    private ArrayList<T> heap;

    public MinHeap() {
        heap = new ArrayList<T>();
    }

    public MinHeap(List<T> list) {
        ArrayList<T> newHeap = new ArrayList<T>(list.size());
        for (T item : list) {
            newHeap.add(item);
        }
        makeHeap(newHeap);
        heap = newHeap;
    }

    // O(1)
    public T peekMin() {
        return heap.get(0);
    }

    // O(log n)
    public T popMin() {
        T min = heap.get(0);
        swap(heap, 0, heap.size() - 1);
        heap.remove(heap.size() - 1);
        heapifyDownward(heap, 0);
        return min;
    }

    // O(log n)
    public void add(T item) {
        heap.add(item);
        heapifyUpward(heap, heap.size() - 1);
    }

    public int size() {
        return heap.size();
    }

    // A heap can clearly be created in O(n log n) time, but we can do better. The `makeHeap` algorithm below creates a
    // heap in O(n) time. Proof via amortized analysis:
    //
    // Sprinkle $2 on each node. Say that we spend $1 each time we visit a node. If we can make a full heap without
    // paying more than the $2*n we started with, then our algorithm's performance is linear. We claim that the final
    // heap will have at least $H left of the original $2*n where H is the height of the heap, so we will have created a
    // heap using O(n) dollars. In fact, we claim that, as we create heaps from the leaves upward, each heap will have
    // $h in it where h is its height.
    //
    // Base case: The leaves all start with $2. We visit each one, spending $1 on each. They are all valid heaps of
    // height 1, and each has $1 remaining.
    //
    // Induction step: Say we have finished building all heaps of height h, and each has $h left. Now we go to transform
    // a tree of height h + 1 into a heap. The parent may be out of place, but the children are valid heaps of height
    // h. We start with $(2*h + 2) dollars since the parent has $2 and each heap has $h left. We visit the parent,
    // spending $1, and swap it with children down the tree a maximum of h times until it is in place. We thus will
    // spend a maximum of $(h + 1) dollars of the original $(2*h + 2) to create the heap of height h + 1, so the heap
    // will have at least $(h + 1) dollars left in it.
    //
    // By induction, the full heap will have at least $H left of the original $2*n, so the algorithm's runtime is
    // linear.
    private void makeHeap(List<T> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            heapifyDownward(list, i);
        }
    }

    private void heapifyDownward(List<T> list, int i) {
        if (i >= list.size()) {
            return;
        }
        T node = list.get(i);
        int leftChildIndex = 2 * i + 1;
        int rightChildIndex = 2 * i + 2;
        if (leftChildIndex >= list.size()) {
            return;
        }
        int minChildIndex = leftChildIndex;
        if (rightChildIndex < list.size()) {
            T leftChild = list.get(leftChildIndex);
            T rightChild = list.get(rightChildIndex);
            if (rightChild.compareTo(leftChild) < 0) {
                minChildIndex = rightChildIndex;
            }
        }
        T minChild = list.get(minChildIndex);
        if (minChild.compareTo(node) < 0) {
            swap(list, i, minChildIndex);
            heapifyDownward(list, minChildIndex);
        }
    }

    private void heapifyUpward(List<T> list, int i) {
        if (i == 0) return;
        T node = list.get(i);
        int parentIndex = (i - 1) / 2;
        if (node.compareTo(list.get(parentIndex)) < 0) {
            swap(list, parentIndex, i);
            heapifyUpward(list, parentIndex);
        }
    }

    private void swap(List<T> list, int a, int b) {
        T temp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, temp);
    }
}
