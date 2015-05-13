package com.cliffcrosland.debugutils;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class DebugUtils {
    public static void printArr(Object[] arr) {
        if (arr.length == 0) {
            System.out.println("{ }");
            return;
        }
        System.out.print("{ ");
        for (int i = 0; i < arr.length - 1; i++) {
            System.out.print(arr[i] + ", ");
        }
        System.out.print(arr[arr.length - 1] + " }");
        System.out.println();
    }
}
