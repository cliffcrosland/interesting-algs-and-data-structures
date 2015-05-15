package com.cliffcrosland.knuthmorrispratt;

import com.cliffcrosland.debugutils.DebugUtils;

/**
 * Created by cliftoncrosland on 5/14/15.
 */
public class KnuthMorrisPrattStringSearch {

    // Find the first index where `needle` occurs in `haystack`. If it doesn't occur, return -1.
    // If n is the length of `haystack` and m is the length of `needle`, the algorithm runs in
    // O(n + m) time, and since n >= m, we can say it runs in O(n) time.
    public static int indexOf(String needle, String haystack) {
        int matchStart = 0;
        int i = 0;
        int[] skipAheadList = computeSkipAheadList(needle);
        while (matchStart + i < haystack.length()) {
            if (i == needle.length()) {
                return matchStart;
            } else if (haystack.charAt(matchStart + i) == needle.charAt(i)) {
                i++;
            } else if (i == 0) {
                matchStart++;
            } else {
                matchStart = matchStart + i - skipAheadList[i];
                i = skipAheadList[i];
            }
        }
        return (i == needle.length())
                ? matchStart
                : -1;
    }

    // Compute the "skip ahead" table for `needle`. The table (call it T) contains len(needle)
    // elements. T[i] indicates the number of elements you can skip ahead from the beginning of
    // `needle` if character i in `needle` does not match in haystack. Performance is O(m) where
    // m is the length of `needle`.
    private static int[] computeSkipAheadList(String needle) {
        int[] skipAheadList = new int[needle.length()];
        skipAheadList[0] = -1;
        int skipAhead = 0;
        for (int i = 1; i < skipAheadList.length; i++) {
            if (needle.charAt(i) == needle.charAt(skipAhead)) {
                skipAheadList[i] = skipAhead++;
            } else {
                skipAheadList[i] = skipAhead = 0;
            }
        }
        return skipAheadList;
    }
}
