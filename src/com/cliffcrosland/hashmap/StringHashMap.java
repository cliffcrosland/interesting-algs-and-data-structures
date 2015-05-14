package com.cliffcrosland.hashmap;

import java.util.ArrayList;

public class StringHashMap<T> {

    private static final int INITIAL_BUCKETS = 32;
    private static final double LOAD_THRESHOLD = 0.8;

    private ArrayList<LinkedListNode<T>> buckets;
    private int size = 0;

    public StringHashMap() {
        buckets = new ArrayList<LinkedListNode<T>>(INITIAL_BUCKETS);
        for (int i = 0; i < INITIAL_BUCKETS; i++) {
            buckets.add(new LinkedListNode<T>());
        }
    }

    public boolean containsKey(String key) {
        return getNodeForKey(key) != null;
    }

    public T get(String key) {
        LinkedListNode<T> node = getNodeForKey(key);
        if (node == null) {
            throw new RuntimeException("Hash map does not contain the key: '" + key + "'");
        }
        return node.value;
    }

    public int size() {
        return size;
    }

    public void set(String key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        boolean added = setInBuckets(buckets, key, value);
        if (added) {
            size++;
        }
        if ((float)size / buckets.size() > LOAD_THRESHOLD) {
            rehashContents();
        }
    }

    public void remove(String key) {
        int hashCode = computeHashCode(key);
        int bucketIndex = hashCode % buckets.size();
        LinkedListNode<T> list = buckets.get(bucketIndex);
        for (LinkedListNode<T> prev = list; prev.next != null; prev = prev.next) {
            LinkedListNode<T> cur = prev.next;
            if (key.equals(cur.key)) {
                prev.next = cur.next;
                size--;
                return;
            }
        }
        throw new RuntimeException("Hash map does not contain the key: '" + key + "'");
    }

    //// Helpers ////

    private void rehashContents() {
        int newNumBuckets = 2 * buckets.size();
        ArrayList<LinkedListNode<T>> newBuckets = new ArrayList<LinkedListNode<T>>(newNumBuckets);
        for (int i = 0; i < newNumBuckets; i++) {
            newBuckets.add(new LinkedListNode<T>());
        }
        for (LinkedListNode<T> list : buckets) {
            for (LinkedListNode<T> cur = list.next; cur != null; cur = cur.next) {
                setInBuckets(newBuckets, cur.key, cur.value);
            }
        }
        buckets = newBuckets;
    }

    private LinkedListNode<T> getNodeForKey(String key) {
        int hashCode = computeHashCode(key);
        int bucketIndex = hashCode % buckets.size();
        LinkedListNode<T> list = buckets.get(bucketIndex);
        for (LinkedListNode<T> cur = list.next; cur != null; cur = cur.next) {
            if (key.equals(cur.key)) {
                return cur;
            }
        }
        return null;
    }

    // Returns true if the key did not exist before and was added. Otherwise, returns false.
    private static <T> boolean setInBuckets(ArrayList<LinkedListNode<T>> buckets, String key, T value) {
        int hashCode = computeHashCode(key);
        int bucketIndex = hashCode % buckets.size();
        LinkedListNode<T> list = buckets.get(bucketIndex);
        for (LinkedListNode<T> cur = list.next; cur != null; cur = cur.next) {
            if (key.equals(cur.key)) {
                cur.value = value;
                return false;
            }
        }
        LinkedListNode<T> newNode = new LinkedListNode<T>(key, value);
        newNode.next = list.next;
        list.next = newNode;
        return true;
    }

    // Say the key `s` has length k.
    // Then the hash code will be:
    //
    // 31^(k-1) * s[0] + 31^(k-2) * s[1] + ... + 31 * s[k-2] + s[k-1]
    //
    // This hash code scheme is the one used in the hashCode() method Java's String class.
    private static int computeHashCode(String key) {
        int hashCode = 0;
        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            hashCode = 31 * hashCode + ch;
        }
        return hashCode;
    }

    private static class LinkedListNode<T> {
        public LinkedListNode next;
        public String key;
        public T value;

        public LinkedListNode() {
        }

        public LinkedListNode(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
