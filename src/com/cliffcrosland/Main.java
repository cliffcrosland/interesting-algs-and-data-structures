package com.cliffcrosland;

import com.cliffcrosland.hashmap.StringHashMap;
import com.cliffcrosland.knuthmorrispratt.KnuthMorrisPrattStringSearch;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        /*
        String haystack = "DCDAAABBABEABBCBEECA";
        String needle = "AABB";
        int index = KnuthMorrisPrattStringSearch.indexOf(needle, haystack);
        System.out.println(index);
        */
        runStressTests();
    }

    private static void runStressTests() {
        long start, end;
        int trials = 10000;
        List<Integer> indicesFoundByMe = new ArrayList<Integer>(trials);
        List<Integer> indicesFoundByJava = new ArrayList<Integer>(trials);
        List<String> haystacks = new ArrayList<String>(trials);
        List<String> needles = new ArrayList<String>(trials);
        for (int i = 0; i < trials; i++) {
            haystacks.add(generateRandomStringFromAlphabet("ABCDE", 100));
            needles.add(generateRandomStringFromAlphabet("ABCDE", 4));
        }

        //start = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            String haystack = haystacks.get(i);
            String needle = needles.get(i);
            int indexFoundByMe = KnuthMorrisPrattStringSearch.indexOf(needle, haystack);
            indicesFoundByMe.add(indexFoundByMe);
        }
        //end = System.currentTimeMillis();
        //System.out.println("My KMP took " + (end - start) + " ms.");


        for (int i = 0; i < trials; i++) {
            String haystack = haystacks.get(i);
            String needle = needles.get(i);
            int indexFoundByJava = haystack.indexOf(needle);
            indicesFoundByJava.add(indexFoundByJava);
        }

        for (int i = 0; i < trials; i++) {
            if (indicesFoundByJava.get(i) != indicesFoundByMe.get(i)) {
                System.out.println("Indices found differ!!!!!");
                System.out.println("Haystack: " + haystacks.get(i));
                System.out.println("Needle: " + needles.get(i));
                System.out.println("Index found by me: " + indicesFoundByMe.get(i));
                System.out.println("Index found by Java: " + indicesFoundByJava.get(i));
            }
        }

        System.out.println("DONE");
    }

    private static String generateRandomStringFromAlphabet(String alphabet, int length) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() < length) {
            int randomIndex = (int)(Math.random() * alphabet.length());
            builder.append(alphabet.charAt(randomIndex));
        }
        return builder.toString();
    }
}
