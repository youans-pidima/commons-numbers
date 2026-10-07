















package org.apache.commons.numbers.arrays;






final class IndexSupport {

    private static final int INSERTION_SORT_SIZE = 20;


    private IndexSupport() {}








    static UpdatingInterval createUpdatingInterval(int[] k, int n) {









        if (n == 2) {
            if (k[0] == k[1]) {
                return newUpdatingInterval(k, 1);
            }
            if (k[1] < k[0]) {
                final int v = k[0];
                k[0] = k[1];
                k[1] = v;
            }
            return newUpdatingInterval(k, 2);
        }












        if (n <= INSERTION_SORT_SIZE) {
            final int unique = Sorting.insertionSortIndices(k, n);
            return newUpdatingInterval(k, unique);
        }

        if (isAscending(k, n)) {




            final int unique = compressDuplicates(k, n);
            return newUpdatingInterval(k, unique);
        }




        int min = k[n - 1];
        int max = min;
        for (int i = n - 2; i >= 0; i--) {
            min = Math.min(min, k[i]);
            max = Math.max(max, k[i]);
        }











































        final int size = max - min + 1;



        int shift = 5;
        if (size > (1 << 20)) {

            shift -= (ceilLog2(size) - 20) >>> 1;
        }

        if ((long) n * n > (size >> shift)) {
            final BitIndexUpdatingInterval interval = new BitIndexUpdatingInterval(min, max);
            for (int i = n; --i >= 0;) {
                interval.set(k[i]);
            }
            return interval;
        }


        final int unique = Sorting.sortIndices(k, n);
        return new KeyUpdatingInterval(k, unique);
    }









    private static boolean isAscending(int[] data, int n) {
        for (int i = 1; i < n; i++) {
            if (data[i] < data[i - 1]) {

                return false;
            }
        }
        return true;
    }










    private static int compressDuplicates(int[] data, int n) {

        int last = 0;
        int top = data[0];
        for (int i = 1; i < n; i++) {
            final int v = data[i];
            if (v == top) {
                continue;
            }
            top = v;
            data[++last] = v;
        }
        return last + 1;
    }









    private static int ceilLog2(int x) {
        return 32 - Integer.numberOfLeadingZeros(x - 1);
    }











    private static UpdatingInterval newUpdatingInterval(int[] k, int n) {
        return new KeyUpdatingInterval(k, n);
    }








    static int countIndices(UpdatingInterval keys, int n) {
        if (keys instanceof KeyUpdatingInterval) {
            return -((KeyUpdatingInterval) keys).size();
        }
        return n;
    }

























    static void checkFromToIndex(int fromIndex, int toIndex, int length) {

        if (fromIndex < 0 || fromIndex > toIndex || toIndex > length) {
            throw new IndexOutOfBoundsException(
                msgRangeOutOfBounds(fromIndex, toIndex, length));
        }
    }









    static void checkIndices(int fromIndex, int toIndex, int[] k) {
        for (final int i : k) {
            checkIndex(fromIndex, toIndex, i);
        }
    }









    static void checkIndex(int fromIndex, int toIndex, int index) {
        if (index < fromIndex || index >= toIndex) {
            throw new IndexOutOfBoundsException(
                msgIndexOutOfBounds(fromIndex, toIndex, index));
        }
    }











    private static String msgRangeOutOfBounds(int fromIndex, int toIndex, int length) {
        return String.format("Range [%d, %d) out of bounds for length %d", fromIndex, toIndex, length);
    }









    private static String msgIndexOutOfBounds(int fromIndex, int toIndex, int index) {
        return String.format("Index %d out of bounds for range [%d, %d)", index, fromIndex, toIndex);
    }
}
