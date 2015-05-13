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

    public T peekMin() {
        return heap.get(0);
    }

    public T popMin() {
        T min = heap.get(0);
        swap(heap, 0, heap.size() - 1);
        heap.remove(heap.size() - 1);
        heapifyDownward(heap, 0);
        return min;
    }

    public void add(T item) {
        heap.add(item);
        heapifyUpward(heap, heap.size() - 1);
    }

    public int size() {
        return heap.size();
    }

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
