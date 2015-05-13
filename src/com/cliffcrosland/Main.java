package com.cliffcrosland;

import com.cliffcrosland.levenshtein.LevenshteinDistance;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String source = "institution";
        String target = "intuition";
        int distance = LevenshteinDistance.distance(source, target);
        System.out.println(distance);
        List<String> steps = LevenshteinDistance.transformationSteps(source, target);
        for (String step : steps) {
            System.out.println(step);
        }
    }
}
