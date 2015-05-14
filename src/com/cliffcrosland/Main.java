package com.cliffcrosland;

import com.cliffcrosland.hashmap.StringHashMap;

public class Main {

    public static void main(String[] args) {
        System.out.println("About to create hash map");
        StringHashMap<Integer> hashMap = new StringHashMap<Integer>();
        System.out.println("About to set elements in hash map");
        for (int i = 0; i < 100; i++) {
            hashMap.set("" + i, i);
        }

        System.out.println("About to get elements from hash map");
        for (int i = 0; i < 100; i++) {
            int value = hashMap.get("" + i);
        }

        System.out.println("Removing all even elements from hash map");
        System.out.println(hashMap.size());
        for (int i = 0; i < 100; i += 2) {
            hashMap.remove("" + i);
        }
        System.out.println(hashMap.size());


        System.out.println("Verifying that all even elements were removed");
        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0 && hashMap.containsKey("" + i)) {
                throw new RuntimeException("The hash map should not contain the even key: " + i);
            }
            if (i % 2 == 1 && !hashMap.containsKey("" + i)) {
                throw new RuntimeException("The hash map should contain the odd key: " + i);
            }
        }
    }
}
