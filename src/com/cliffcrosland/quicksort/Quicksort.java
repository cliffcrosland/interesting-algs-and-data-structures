package com.cliffcrosland.quicksort;

public class Quicksort {
    public static void sort(Comparable[] arr) {
        quicksort(arr, 0, arr.length - 1);
    }

    // Average, Best case O(n log n). Worst case O(n^2) if pivots are always extremes, but that is unlikely.
    private static void quicksort(Comparable[] arr, int start, int end) {
        if (start >= end) return;
        // Pivot value. We want to move everything smaller than this value to the left, everything not smaller to the
        // right.
        int randIndex = start + (int)(Math.random() * (end - start + 1));
        swap(arr, start, randIndex);
        Comparable pivot = arr[start];
        // Marks the boundary between the elements less than the pivot and the elements not less than the pivot.
        int boundaryIndex = start + 1;
        for (int i = start + 1; i <= end; i++) {
            if (arr[i].compareTo(pivot) < 0) {
                swap(arr, i, boundaryIndex++);
            }
        }
        boundaryIndex--;
        swap(arr, start, boundaryIndex);
        quicksort(arr, start, boundaryIndex - 1);
        quicksort(arr, boundaryIndex + 1, end);
    }

    private static void swap(Comparable[] arr, int a, int b) {
        Comparable temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

}
