















package org.apache.commons.numbers.examples.jmh.arrays;







final class IndexIntervals {

    private static final int KEY_ANALYSIS_SIZE = 10;

    private static final int INDICES_INSERTION_SORT_SIZE = 20;






    private static final int BINARY_SEARCH_SIZE = 2048;


    private IndexIntervals() {}









    static SearchableInterval anyIndex() {
        return AnyIndex.INSTANCE;
    }









    static SearchableInterval2 anyIndex2() {
        return AnyIndex.INSTANCE;
    }








    static UpdatingInterval interval(int k) {
        return new PointInterval(k);
    }












    static UpdatingInterval interval(int left, int right) {

        final int l = left < right ? left : right;
        final int r = left < right ? right : left;
        return new RangeInterval(l, r);
    }








    static SearchableInterval createSearchableInterval(int[] k, int n) {




        if (n > KEY_ANALYSIS_SIZE) {



















            int min = k[n - 1];
            int max = min;
            for (int i = n - 1; --i >= 0;) {
                min = Math.min(min, k[i]);
                max = Math.max(max, k[i]);
            }






















            final int size = max - min + 1;



            int shift = 5;
            if (size > (1 << 20)) {

                shift -= (ceilLog2(size) - 20) >>> 1;
            }

            if ((long) n * n > (size >> shift)) {


                final IndexSet interval = IndexSet.ofRange(min, max);
                for (int i = n; --i >= 0;) {
                    interval.set(k[i]);
                }
                return interval;
            }





            if (n > BINARY_SEARCH_SIZE) {
                final int unique = Sorting.sortIndices2(k, n);
                return BinarySearchKeyInterval.of(k, unique);
            }


        }




        final int unique = Sorting.sortIndices2(k, n);

        return ScanningKeyInterval.of(k, unique);
    }








    static UpdatingInterval createUpdatingInterval(int[] k, int n) {









        if (n < 3) {
            if (n == 1 || k[0] == k[1]) {

                return IndexIntervals.interval(k[0]);
            }

            if (Math.abs(k[0] - k[1]) == 1) {

                return IndexIntervals.interval(k[0], k[1]);
            }

            if (k[1] < k[0]) {
                final int v = k[0];
                k[0] = k[1];
                k[1] = v;
            }
            return KeyUpdatingInterval.of(k, 2);
        }












        if (n <= INDICES_INSERTION_SORT_SIZE) {
            final int unique = Sorting.sortIndicesInsertionSort(k, n);
            return KeyUpdatingInterval.of(k, unique);
        }

        if (isAscending(k, n)) {




            final int unique = compressDuplicates(k, n);
            return KeyUpdatingInterval.of(k, unique);
        }




        int min = k[n - 1];
        int max = min;
        for (int i = n - 1; --i >= 0;) {
            min = Math.min(min, k[i]);
            max = Math.max(max, k[i]);
        }











































        final int size = max - min + 1;



        int shift = 5;
        if (size > (1 << 20)) {

            shift -= (ceilLog2(size) - 20) >>> 1;
        }

        if ((long) n * n > (size >> shift)) {


            final BitIndexUpdatingInterval interval = BitIndexUpdatingInterval.ofRange(min, max);
            for (int i = n; --i >= 0;) {
                interval.set(k[i]);
            }
            return interval;
        }


        final int unique = Sorting.sortIndicesHashIndexSet(k, n);
        return KeyUpdatingInterval.of(k, unique);
    }









    private static boolean isAscending(int[] data, int n) {
        for (int i = 0; ++i < n;) {
            if (data[i] < data[i - 1]) {

                return false;
            }
        }
        return true;
    }










    private static int compressDuplicates(int[] data, int n) {

        int last = 0;
        int top = data[0];
        for (int i = 0; ++i < n;) {
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




    private static final class AnyIndex implements SearchableInterval, SearchableInterval2 {

        static final AnyIndex INSTANCE = new AnyIndex();

        @Override
        public int left() {
            return 0;
        }

        @Override
        public int right() {
            return Integer.MAX_VALUE - 1;
        }

        @Override
        public int previousIndex(int k) {
            return k;
        }

        @Override
        public int nextIndex(int k) {
            return k;
        }

        @Override
        public int split(int ka, int kb, int[] upper) {
            upper[0] = kb + 1;
            return ka - 1;
        }




        @Override
        public int start() {
            return 0;
        }

        @Override
        public int end() {
            return Integer.MAX_VALUE - 1;
        }

        @Override
        public int index(int i) {
            return i;
        }

        @Override
        public int previous(int i, int k) {
            return k;
        }

        @Override
        public int next(int i, int k) {
            return k;
        }

        @Override
        public int split(int i1, int i2, int ka, int kb, int[] upper) {
            upper[0] = kb + 1;
            return ka - 1;
        }
    }




    static final class PointInterval implements UpdatingInterval {

        private final int index;




        PointInterval(int k) {
            this.index = k;
        }

        @Override
        public int left() {
            return index;
        }

        @Override
        public int right() {
            return index;
        }




        @Override
        public int updateLeft(int k) {
            throw new UnsupportedOperationException("updateLeft should not be called");
        }

        @Override
        public int updateRight(int k) {
            throw new UnsupportedOperationException("updateRight should not be called");
        }

        @Override
        public UpdatingInterval splitLeft(int ka, int kb) {
            throw new UnsupportedOperationException("splitLeft should not be called");
        }

        @Override
        public UpdatingInterval splitRight(int ka, int kb) {
            throw new UnsupportedOperationException("splitRight should not be called");
        }
    }




    static final class RangeInterval implements UpdatingInterval {

        private int left;

        private int right;





        RangeInterval(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public int left() {
            return left;
        }

        @Override
        public int right() {
            return right;
        }

        @Override
        public int updateLeft(int k) {

            left = k;
            return k;
        }

        @Override
        public int updateRight(int k) {

            right = k;
            return k;
        }

        @Override
        public UpdatingInterval splitLeft(int ka, int kb) {

            final int lower = left;
            left = kb + 1;
            return new RangeInterval(lower, ka - 1);
        }

        @Override
        public UpdatingInterval splitRight(int ka, int kb) {

            final int upper = right;
            right = ka - 1;
            return new RangeInterval(kb + 1, upper);
        }
    }
}
