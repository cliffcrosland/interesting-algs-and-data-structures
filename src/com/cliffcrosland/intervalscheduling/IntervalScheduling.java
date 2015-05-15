package com.cliffcrosland.intervalscheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/15/15.
 */
public class IntervalScheduling {
    // Greedy algorithm that finds the maximum number of intervals that can be mutually scheduled without overlapping.
    // O(n log n) to sort original list of intervals by finish time, plus O(n) to add non-overlapping intervals
    // greedily.
    public static List<Interval> getOptimalIntervalSchedule(List<Interval> intervals) {
        if (intervals.size() == 0) {
            return new ArrayList<Interval>();
        }
        List<Interval> solution = new ArrayList<Interval>();
        List<Interval> intervalsSortedByFinishTime = new ArrayList<Interval>(intervals);
        intervalsSortedByFinishTime.sort(new Comparator<Interval>() {
            @Override
            public int compare(Interval a, Interval b) {
                return a.finish - b.finish;
            }
        });
        solution.add(intervalsSortedByFinishTime.get(0));
        for (int i = 1; i < intervalsSortedByFinishTime.size(); i++) {
            Interval latestIntervalInSolution = solution.get(solution.size() - 1);
            Interval interval = intervalsSortedByFinishTime.get(i);
            if (!latestIntervalInSolution.overlapsWith(interval)) {
                solution.add(interval);
            }
        }
        return solution;
    }
}
