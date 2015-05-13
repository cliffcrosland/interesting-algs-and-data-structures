package com.cliffcrosland.levenshtein;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cliftoncrosland on 5/13/15.
 */
public class LevenshteinDistance {

    public static int distance(String source, String target) {
        int[][] distances = computeLevenshteinDistances(source, target);
        return distances[source.length()][target.length()];
    }

    public static List<String> transformationSteps(String source, String target) {
        int[][] distances = computeLevenshteinDistances(source, target);
        List<String> alignmentSteps = new ArrayList<String>();
        int i = source.length();
        int j = target.length();
        while (i > 0 && j > 0) {
            int minimum = minimum(
                    distances[i - 1][j],
                    distances[i][j - 1],
                    distances[i - 1][j - 1]);
            if (distances[i][j] == minimum) {
                alignmentSteps.add(0, "match " + source.charAt(i - 1));
                i--; j--;
            } else if (distances[i - 1][j - 1] == minimum) {
                alignmentSteps.add(0, "replace " + source.charAt(i - 1) + " with " + target.charAt(j - 1));
                i--; j--;
            } else if (distances[i][j - 1] == minimum) {
                alignmentSteps.add(0, "insert " + target.charAt(j - 1));
                j--;
            }  else {
                alignmentSteps.add(0, "delete " + source.charAt(i - 1));
                i--;
            }
        }
        while (i > 0) {
            alignmentSteps.add(0, "delete " + source.charAt(i - 1));
            i--;
        }
        while (j > 0) {
            alignmentSteps.add(0, "insert " + target.charAt(j - 1));
            j--;
        }
        return alignmentSteps;
    }

    private static int[][] computeLevenshteinDistances(String source, String target) {
        // distances[i][j] will hold the Levenshtein distance between the first i characters of the source string and
        // the first j characters of the target string.
        int[][] distances = new int[source.length() + 1][target.length() + 1];
        distances[0][0] = 0;
        // source prefixes can be transformed into empty string by deleting all characters.
        for (int i = 1; i <= source.length(); i++) {
            distances[i][0] = i;
        }
        // empty source prefix can be transformed into target prefixes by inserting every character.
        for (int j = 1; j <= target.length(); j++) {
            distances[0][j] = j;
        }

        for (int i = 1; i <= source.length(); i++) {
            for (int j = 1; j <= target.length(); j++) {
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    // characters match, no cost
                    distances[i][j] = distances[i - 1][j - 1];
                } else {
                    distances[i][j] = minimum(
                            distances[i - 1][j] + 1,      // deletion
                            distances[i][j - 1] + 1,      // insertion
                            distances[i - 1][j - 1] + 1); // replacement
                }
            }
        }
        return distances;
    }

    private static int minimum(int... values) {
        int minSoFar = Integer.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            minSoFar = Math.min(minSoFar, values[i]);
        }
        return minSoFar;
    }

    private static void printMatrix(int[][] matrix) {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[0].length; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }

    }
}
