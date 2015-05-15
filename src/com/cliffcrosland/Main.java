package com.cliffcrosland;

import com.cliffcrosland.hashmap.StringHashMap;
import com.cliffcrosland.knuthmorrispratt.KnuthMorrisPrattStringSearch;

public class Main {

    public static void main(String[] args) {
        String haystack = "ABDABEABFAAABCBBEEEAB";
        String needle = "ABC";
        int index = KnuthMorrisPrattStringSearch.indexOf(needle, haystack);
        System.out.println(index);
    }
}
