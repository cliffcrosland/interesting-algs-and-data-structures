package com.cliffcrosland;

import com.cliffcrosland.quickselect.Quickselect;

public class Main {

    public static void main(String[] args) {
        Integer[] values = new Integer[] {4, 1, 45, 12, 3, 6, 23, 5, 6};
        for (int n = values.length; n >= 1; n--) {
            System.out.println(Quickselect.select(values, n));
        }
    }
}
