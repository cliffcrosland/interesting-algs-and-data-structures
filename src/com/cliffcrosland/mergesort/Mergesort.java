package com.cliffcrosland.mergesort;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class Mergesort {

    public static void sort(Comparable[] arr) {
        mergesort(arr, 0, arr.length - 1);
    }

    // Best, average, worst case run-times are O(n log n). O(n) storage required. If we used O(1) storage and merged
    // in-place, then the merge step would run in O(n) in the best case, and O(n^2) in the worst.
    private static void mergesort(Comparable[] arr, int start, int end) {
        if (start >= end) return;
        int mid = (start + end) / 2;
        mergesort(arr, start, mid);
        mergesort(arr, mid + 1, end);
        merge(arr, start, mid, mid + 1, end);
    }

    private static void merge(Comparable[] arr, int aStart, int aEnd, int bStart, int bEnd) {
        Comparable[] sorted = new Comparable[bEnd - aStart + 1];
        int sortedIndex = 0;
        int a = aStart;
        int b = bStart;
        while (a <= aEnd && b <= bEnd) {
            if (arr[a].compareTo(arr[b]) < 0) {
                sorted[sortedIndex++] = arr[a++];
            } else {
                sorted[sortedIndex++] = arr[b++];
            }
        }
        while (a <= aEnd) {
            sorted[sortedIndex++] = arr[a++];
        }
        while (b <= bEnd) {
            sorted[sortedIndex++] = arr[b++];
        }
        sortedIndex = 0;
        for (int i = aStart; i <= bEnd; i++) {
            arr[i] = sorted[sortedIndex++];
        }
    }
}
