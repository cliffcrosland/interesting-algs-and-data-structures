package com.cliffcrosland;


import com.cliffcrosland.heap.MinHeap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Integer> values = Arrays.asList(4, 1, 45, 12, 3, 6, 23, 5, 6);
        MinHeap<Integer> heap = new MinHeap<Integer>(values);
        while (heap.size() > 0) {
            System.out.println(heap.popMin());
        }



    }
}
