
















package org.apache.commons.numbers.examples.jmh.arrays;

























final class HashIndexSet {

    private static final String INVALID_INDEX = "Invalid index: ";

    private static final int MAX_CAPACITY = 1 << 29;

    private static final int MIN_SIZE = 16;









    private static final int PHI = 0x9e3779b9;


    private final int[] set;

    private int size;










    HashIndexSet(int capacity) {
        if (capacity > MAX_CAPACITY) {
            throw new IllegalArgumentException("Unsupported capacity: " + capacity);
        }


        set = new int[nextPow2(Math.max(MIN_SIZE, capacity * 2))];
    }













    static long memoryFootprint(int capacity) {
        if (capacity <= (MIN_SIZE >> 1)) {

            return MIN_SIZE << 2;
        }


        return Integer.toUnsignedLong(nextPow2(capacity)) << 3;
    }














    private static int nextPow2(int value) {
        int result = value - 1;
        result |= result >>> 1;
        result |= result >>> 2;
        result |= result >>> 4;
        result |= result >>> 8;
        return (result | (result >>> 16)) + 1;
    }








    boolean add(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(INVALID_INDEX + index);
        }
        final int[] keys = set;
        final int key = ~index;
        final int mask = keys.length - 1;
        int pos = mix(index) & mask;
        int curr = keys[pos];
        if (curr < 0) {
            if (curr == key) {

                return false;
            }

            while ((curr = keys[pos = (pos + 1) & mask]) < 0) {
                if (curr == key) {

                    return false;
                }
            }
        }

        keys[pos] = key;

        if (++size > (mask + 1) >>> 1) {



            throw new IllegalStateException("Functional capacity exceeded: " + (keys.length >>> 1));
        }
        return true;
    }











    boolean contains(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException(INVALID_INDEX + index);
        }
        final int[] keys = set;
        final int mask = keys.length - 1;
        int pos = mix(index) & mask;
        int curr = keys[pos];
        if (curr == 0) {
            return false;
        }
        final int key = ~index;
        if (curr == key) {
            return true;
        }

        while (true) {
            pos = (pos + 1) & mask;
            curr = keys[pos];
            if (curr == 0) {

                return false;
            }
            if (curr == key) {
                return true;
            }
        }
    }










    private static int mix(int x) {
        final int h = x * PHI;
        return h ^ (h >>> 16);
    }






    int size() {
        return size;
    }

















    int toArray(int[] a) {
        final int[] keys = set;
        int c = 0;
        for (final int key : keys) {
            if (key < 0) {
                a[c++] = ~key;
            }
        }

        return c;
    }
}
