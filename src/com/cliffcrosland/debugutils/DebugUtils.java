package com.cliffcrosland.debugutils;

import java.util.List;

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

    public static void printListOfPoints(List<double[]> points) {
        System.out.print("{ ");
        for (int i = 0; i < points.size(); i++) {
            double[] point = points.get(i);
            System.out.print("( ");
            for (int j = 0; j < point.length; j++) {
                System.out.print(point[j] + " ");
            }
            System.out.print(") ");
        }
        System.out.print("}");
        System.out.println();
    }
}
