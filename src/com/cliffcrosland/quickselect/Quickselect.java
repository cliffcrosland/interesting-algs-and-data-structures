package com.cliffcrosland.quickselect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class Quickselect {
    public static <T extends Comparable> T select(T[] arr, int n) {
        if (n < 1 || n > arr.length) {
            throw new IllegalArgumentException("n must be in the inclusive range [1, len]");
        }
        List<T> copy = new ArrayList<T>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            copy.add(arr[i]);
        }
        return select(copy, 0, arr.length - 1, n);
    }

    // Select the n-th smallest element
    private static <T extends Comparable> T select(List<T> arr, int start, int end, int n) {
        // Pivot value. We want to move everything smaller than this value to the left, everything not smaller to the
        // right.
        int randIndex = start + (int)(Math.random() * (end - start + 1));
        swap(arr, start, randIndex);
        Comparable pivot = arr.get(start);
        // Marks the boundary between the elements less than the pivot and the elements not less than the pivot.
        int boundaryIndex = start + 1;
        for (int i = start + 1; i <= end; i++) {
            if (arr.get(i).compareTo(pivot) < 0) {
                swap(arr, i, boundaryIndex++);
            }
        }
        boundaryIndex--;
        swap(arr, start, boundaryIndex);
        int rankOfPivot = boundaryIndex + 1;
        if (rankOfPivot == n) {
            return arr.get(boundaryIndex);
        } else if (rankOfPivot < n) {
            return select(arr, boundaryIndex + 1, end, n);
        } else {
            return select(arr, start, boundaryIndex - 1, n);
        }
    }

    private static <T extends Comparable> void swap(List<T> arr, int a, int b) {
        T temp = arr.get(a);
        arr.set(a, arr.get(b));
        arr.set(b, temp);
    }
}
