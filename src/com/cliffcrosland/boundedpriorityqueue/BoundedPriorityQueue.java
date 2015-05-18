package com.cliffcrosland.boundedpriorityqueue;

import com.cliffcrosland.minmaxheap.MinMaxHeap;

import java.util.ArrayList;
import java.util.List;

/*
 * Priority queue that will not grow beyond a certain size. Will eject the item with max priority if the queue grows
 * beyond the max-size. O(1) lookup for min and max. O(log n) removing min or max. Backing data structure is a min-max
 * heap.
 */
public class BoundedPriorityQueue<T> {

    private int maxSize;
    private MinMaxHeap<PriorityQueueNode<T>> heap;

    public BoundedPriorityQueue(int maxSize) {
        this.maxSize = maxSize;
        this.heap = new MinMaxHeap<PriorityQueueNode<T>>();
    }

    // O(1) lookup min priority
    public double peekMinPriority() {
        return heap.peekMin().priority;
    }

    // O(1) lookup max priority
    public double peekMaxPriority() {
        return heap.peekMax().priority;
    }

    // O(1) lookup min value
    public T peekMinValue() {
        return heap.peekMin().value;
    }

    // O(1) lookup max value
    public T peekMaxValue() {
        return heap.peekMax().value;
    }

    // O(log n) pop min value
    public T popMinValue() {
        return heap.popMin().value;
    }

    // O(log n) pop max value
    public T popMaxValue() {
        return heap.popMax().value;
    }

    // O(log n) - Add item. If the priority queue has exceeded its bounds, pop the maximum, and return the value stored
    // there.
    public T add(T value, double priority) {
        heap.add(new PriorityQueueNode<T>(value, priority));
        if (heap.size() > maxSize) {
            return heap.popMax().value;
        }
        return null;
    }

    // O(1) - get size
    public int size() {
        return heap.size();
    }

    // O(1) - lookup max allowed size
    public int maxSize() {
        return maxSize;
    }

    // O(1) - is empty?
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // O(1) - is full?
    public boolean isFull() {
        return heap.size() == maxSize;
    }

    // O(n log n) - Create a list of all of the items in the priority queue, ordered by priority.
    public List<T> toListOrderedByPriority() {
        List<PriorityQueueNode<T>> nodes = new ArrayList<PriorityQueueNode<T>>(heap.size());
        List<T> ret = new ArrayList<T>(heap.size());
        while (heap.size() > 0) {
            PriorityQueueNode<T> node = heap.popMin();
            nodes.add(node);
            ret.add(node.value);
        }
        for (PriorityQueueNode<T> node : nodes) {
            heap.add(node);
        }
        return ret;
    }

    private class PriorityQueueNode<T> implements Comparable {
        public T value;
        public double priority;

        public PriorityQueueNode(T value, double priority) {
            this.value = value;
            this.priority = priority;
        }

        @Override
        public int compareTo(Object o) {
            PriorityQueueNode<T> other = (PriorityQueueNode<T>)o;
            if (this.priority < other.priority) {
                return -1;
            } else if (this.priority > other.priority) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
