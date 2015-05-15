package com.cliffcrosland.intervalscheduling;

import java.util.Date;

/**
 * Created by cliftoncrosland on 5/15/15.
 */
public class Interval {
    public int start;
    public int finish;

    public Interval(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    public boolean overlapsWith(Interval other) {
        if (other == null) {
            throw new IllegalArgumentException("Other interval cannot be null");
        }
        return isBetween(other.start, start, finish) ||
               isBetween(start, other.start, other.finish);

    }

    private static boolean isBetween(int value, int start, int finish) {
        return value >= start && value < finish;
    }
}
