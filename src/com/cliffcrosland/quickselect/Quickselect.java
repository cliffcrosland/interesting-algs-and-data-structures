package com.cliffcrosland.quickselect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class Quickselect {

    public static <T extends Comparable> T select(List<T> list, int n) {
        Comparator<T> comparableComparator = new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1.compareTo(o2);
            }
        };
        return select(list, n, comparableComparator);
    }

    public static <T> T select(List<T> list, int n, Comparator<T> comparator) {
        List<T> copy = new ArrayList<T>(list);
        return selectInPlace(copy, n, comparator);
    }

    public static <T> T selectInPlace(List<T> list, int n, Comparator<T> comparator) {
        if (n < 1 || n > list.size()) {
            throw new IllegalArgumentException("n must be in the inclusive range [1, len]. n was: " + n);
        }
        return select(list, 0, list.size() - 1, n, comparator);
    }

    // Select the n-th smallest element. Best, average case O(n). Worst case O(n^2)
    private static <T> T select(List<T> arr, int start, int end, int n, Comparator<T> comparator) {
        // Pivot value. We want to move everything smaller than this value to the left, everything not smaller to the
        // right.
        int randIndex = start + (int)(Math.random() * (end - start + 1));
        swap(arr, start, randIndex);
        T pivot = arr.get(start);
        // Marks the boundary between the elements less than the pivot and the elements not less than the pivot.
        int boundaryIndex = start + 1;
        for (int i = start + 1; i <= end; i++) {
            if (comparator.compare(arr.get(i), pivot) < 0) {
                swap(arr, i, boundaryIndex++);
            }
        }
        boundaryIndex--;
        swap(arr, start, boundaryIndex);
        int rankOfPivot = boundaryIndex + 1;
        if (rankOfPivot == n) {
            return arr.get(boundaryIndex);
        } else if (rankOfPivot < n) {
            return select(arr, boundaryIndex + 1, end, n, comparator);
        } else {
            return select(arr, start, boundaryIndex - 1, n, comparator);
        }
    }

    private static <T> void swap(List<T> arr, int a, int b) {
        T temp = arr.get(a);
        arr.set(a, arr.get(b));
        arr.set(b, temp);
    }
}
