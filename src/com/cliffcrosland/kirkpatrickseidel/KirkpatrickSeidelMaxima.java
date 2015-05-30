package com.cliffcrosland.kirkpatrickseidel;

import com.cliffcrosland.quickselect.Quickselect;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Cliff on 5/30/2015.
 */
public class KirkpatrickSeidelMaxima {

    // Say that a 2D point p "dominates" another 2D point q if both its coordinates are greater than q's
    // (i.e. p.x > q.x and p.y > q.y).
    //
    // # PROBLEM #
    //
    // Find all of the "undominated" maxima in a set of points. These will be the "north-eastern" frontier of a set of
    // points, and none of these maxima are "dominated" by any other points.
    //
    // # ALGORITHM - Divide and conquer #
    // - Base case: if there are <= 1 point, add it to the output. It's a globally undominated maximum.
    // - Find the median point by x-coordinate (could equivalently pick y if you wanted to). Divide the region in
    //   half at that median. Call the two new regions L and R (for left and right).
    // - Look through R and find the point with the highest y coordinate. That is a globally undominated maximum. Add
    //   it to your output.
    // - Delete every point in both L and R that is dominated by that maximum (i.e. every point that has smaller x
    //   coordinate and y coordinate).
    // - Recurse in region L, and recurse in region R.
    //
    // # PERFORMANCE #
    // Performance can be easily shown to be O(n log n). Also easily shown to be O(n log h) where h is the number of
    // output points. For some good cases (like where there is a single maximum undominated point), it runs in linear
    // time. So some inputs do better than others. Can we predict what the run-time will be given a particular input?
    // The answer is: yep.
    //
    // Let's describe what the run-time performance for a given input:
    // - Consider an input of points, call it S.
    // - Draw a "stair-case" starting at the top-left maximum of S and ending at the bottom-right maximum of S, each
    //   line drawn vertically or horizontally.
    // - Draw k boxes such that the rest of S (the non-maxima) are contained within them. Note that each of the k boxes
    //   is beneath the "stair-case". Call these boxes S1, S2, ... Si, ... Sk. Let's call each possible drawing of boxes
    //   where all of S's points (other than maxima) lie in a box a "legal box partition".
    // - Let there be an interesting looking function f that takes a legal box partition and produces the following sum
    //   over the boxes in the partition (note that |Si| is the number of points in bucket Si):
    //   f(legal_box_partition) = SUM_(i=1)^(k) |Si| * log (n / |Si|)
    // - Let MIN_SUM be the minimum value of f(legal_box_partition) over all possible legal box partitions for S.
    // - THEN, the runtime of the algorithm is O(MIN_SUM).
    // - What does this mean? Well, with a crappy legal box partition, you'd use n boxes, and put each point in a box
    //   by itself. Then trivially, you get SUM_(i=1)^(n) 1 * log (n / 1) = n log n, which is a trivial worst case
    //   upper-bound. However, if you get a wonderful case where there is one maximum, meaning that the rest of the
    //   points can be contained in a single box, then the sum becomes SUM_(i=1)^1 n * 1 = n, which is a great case -
    //   linear. If you have an input that can be drawn into a few boxes beneath the stair-case, you get pretty close to
    //   linear time.
    //
    //   Hand-wave-y proof of the run-time performance:
    //   - Given a partition into S1, S2, ..., Si, ... Sk boxes beneath the stair-case, we want to show that the runtime
    //     is bounded by that crazy sum SUM_(i=1)^(k) |Si| * log (n / |Si|).
    //   - Consider the recursive call tree structure. At each level in the recursive call tree, you divide the
    //     recursive calls in the previous level in halves, and do at most linear work on each level. We want to see how
    //     much each bucket has left at each level.
    //   - The big idea is that an arbitrary bucket, Si, will have fewer and fewer points left alive in it as we get
    //     deeper in the recursive call tree because its points will get lopped off as we find maxima.
    //   - Say we get to recursive call tree level j:
    //     - Si will definitely have less than or equal to what it started out with, which is |Si|.
    //     - Si will have less than or equal to O(n / 2^j) points left in it.
    //       - WHY? At recursive call level j, there are 2^j recursive calls. The n original points are divided up into
    //         2^j recursive calls, evenly split between them because we partition by medians in each call. So each
    //         recursive call contains n / 2^j elements. The points still remaining in the box will be between two
    //         maxima, one picked in each recursive call, so the points in the box Si might span at most 2 recursive
    //         calls on level j, so there may be at most 2 * n / 2^j points in Si at level j. We drop the 2 because
    //         we're just interested in the fact that the number of points left in Si at level j will be O(n / 2^j).
    //   - OK cool, now that we know that at level j, there will be at most O(n / 2^j) points left in Si. How does this
    //     help us get our crazy sum? Well, as we look at Si's size change over time with each level in the recursion,
    //     we note that in the first few levels, Si might not decrease very much at all, but as soon as n / 2^j becomes
    //     less than Si's original size, then the size of Si at each new recursive level drops off really fast. The work
    //     that Si contributes to the algorithm looks like this at each level in the recursion:
    //                              (first several levels, unchanged)  (then the n/2^j kicks in, telescopes to |Si|)
    //       - work_Si_contributes = |Si| + |Si| + |Si| + ... + |Si| + |Si|/2 + |Si|/4 + |Si|/8 + ... + 1
    //       - work_Si_contributes = |Si| + |Si| + |Si| + ... + |Si| + (|Si|)
    //
    //  - So now the question is, how many |Si| terms are there? How many levels do there need to be before n/2^j
    //    is less than |Si|? The answer is log(n / |Si|) levels (i.e. the number of halvings it takes to get from
    //    n/|Si| down to 1.
    //  - And that leads us straight to our SUM! Our function f says that we do |Si| work log(n / |Si|) times. Summed
    //    over all of the boxes, that gives us the sum: f(legal_box_partition) = SUM_(i=1)^(k) |Si| * log (n / |Si|).
    //  - And if we could somehow look through every possible legal partitioning of S into boxes, and find the one with
    //    the least value for f(legal_box_partition), our algorithm would run in Big-Oh of that value. Cool!
    //  - By the way, that SUM looks hairy, but it simply means that we can do close to linear if the input can be
    //    partitioned into a few number of big boxes beneath the stair-case nicely.
    public static List<Point2> findUndominatedMaxima(List<Point2> points) {
        List<Point2> copyOfPoints = new ArrayList<>(points);
        List<Point2> maxima = new ArrayList<>();
        recursiveFindUndominatedMaxima(copyOfPoints, maxima);
        return maxima;
    }

    private static void recursiveFindUndominatedMaxima(List<Point2> points, List<Point2> maxima) {
        if (points.size() == 0) return;
        if (points.size() == 1) {
            maxima.add(points.get(0));
            return;
        }
        Quickselect.selectInPlace(points, points.size() / 2, xComparator());
        List<Point2> left = getRange(points, 0, points.size() / 2);
        List<Point2> right = getRange(points, points.size() / 2, points.size());
        Point2 maximum = findMax(right, yComparator());
        maxima.add(maximum);
        left = withoutDominated(left, maximum);
        right = withoutDominated(right, maximum);
        recursiveFindUndominatedMaxima(left, maxima);
        recursiveFindUndominatedMaxima(right, maxima);
    }

    private static Point2 findMax(List<Point2> points, Comparator<Point2> comp) {
        Point2 max = null;
        for (Point2 point : points) {
            if (max == null || comp.compare(point, max) > 0) {
                max = point;
            }
        }
        return max;
    }

    private static List<Point2> withoutDominated(List<Point2> points, Point2 maximum) {
        List<Point2> ret = new ArrayList<>();
        for (Point2 point : points) {
            if (maximum.x >= point.x && maximum.y >= point.y) {
                continue;
            }
            ret.add(point);
        }
        return ret;
    }

    private static List<Point2> getRange(List<Point2> points, int start, int end) {
        List<Point2> ret = new ArrayList<>();
        for (int i = start; i < end; i++) {
            ret.add(points.get(i));
        }
        return ret;
    }

    private static Comparator<Point2> xComparator() {
        return (a, b) -> a.x - b.x;
    }

    private static Comparator<Point2> yComparator() {
        return (a, b) -> a.y - b.y;
    }
}
