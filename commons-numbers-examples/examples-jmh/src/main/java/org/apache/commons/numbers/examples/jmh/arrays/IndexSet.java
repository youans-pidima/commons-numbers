















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.function.IntConsumer;























final class IndexSet implements PivotCache, SearchableInterval, SearchableInterval2 {

    private static final long LONG_MASK = -1L;

    private static final int DIVIDE_BY_64 = 6;


    private static final int UPPER_DEFAULT = Integer.MAX_VALUE;


    private final long[] data;


    private final int left;

    private final int right;


    private int lowerPivot = -1;


    private int upperPivot = UPPER_DEFAULT;







    private IndexSet(int left, int right) {
        this.left = left;
        this.right = right;




        data = new long[getLongIndex(right - left) + 1];
    }









    static IndexSet ofRange(int left, int right) {
        if (left < 0) {
            throw new IllegalArgumentException("Invalid lower index: " + left);
        }
        checkRange(left, right);
        return new IndexSet(left, right);
    }









    static IndexSet of(int[] indices) {
        return of(indices, indices.length);
    }










    static IndexSet of(int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        int min = indices[0];
        int max = min;
        for (int i = 1; i < n; i++) {
            min = Math.min(min, indices[i]);
            max = Math.max(max, indices[i]);
        }
        final IndexSet set = IndexSet.ofRange(min, max);
        for (int i = 0; i < n; i++) {
            set.set(indices[i]);
        }
        return set;
    }
















    static long memoryFootprint(int left, int right) {
        return (getLongIndex(right - left) + 1L) * Long.BYTES;
    }














    private static int getLongIndex(final int bitIndex) {






        return bitIndex >> DIVIDE_BY_64;
    }














    private static long getLongBit(final int bitIndex) {




        return 1L << bitIndex;
    }
















    public int cardinality2() {
        int c = 0;
        for (long x : data) {

            x = (x | (x >>> 1)) & 0b0101010101010101010101010101010101010101010101010101010101010101L;

            c += Long.bitCount(x);
        }

        return c << 1;
    }














    public int cardinality4() {
        int c = 0;
        for (long x : data) {

            x = x | (x >>> 1);
            x = (x | (x >>> 2)) & 0b0001000100010001000100010001000100010001000100010001000100010001L;


            c += Long.bitCount(x);
        }

        return c << 2;
    }














    public int cardinality8() {
        int c = 0;
        for (long x : data) {

            x = x | (x >>> 1);
            x = x | (x >>> 2);
            x = (x | (x >>> 4)) & 0b0000000100000001000000010000000100000001000000010000000100000001L;


            c += Long.bitCount(x);
        }

        return c << 3;
    }














    public int cardinality16() {
        int c = 0;
        for (long x : data) {

            x = x | (x >>> 1);
            x = x | (x >>> 2);
            x = x | (x >>> 4);
            x = (x | (x >>> 8)) & 0b0000000000000001000000000000000100000000000000010000000000000001L;




            x = x + (x >>> 16);
            x = x + (x >>> 32);

            c += (int) x & 0b111;
        }

        return c << 4;
    }














    public int cardinality32() {
        int c = 0;
        for (final long x : data) {

            c += (int) x != 0 ? 1 : 0;
            c += (x >>> 32) != 0L ? 1 : 0;
        }

        return c << 5;
    }














    public int cardinality64() {
        int c = 0;
        for (final long x : data) {

            c += x != 0L ? 1 : 0;
        }

        return c << 6;
    }








    public int cardinality() {
        int c = 0;
        for (final long x : data) {
            c += Long.bitCount(x);
        }
        return c;
    }







    public boolean get(int bitIndex) {

        final int index = bitIndex - left;
        final int i = getLongIndex(index);
        final long m = getLongBit(index);
        return (data[i] & m) != 0;
    }









    public void set(int bitIndex) {

        final int index = bitIndex - left;
        final int i = getLongIndex(index);
        final long m = getLongBit(index);
        data[i] |= m;
    }
















    public void set(int leftIndex, int rightIndex) {
        final int l = leftIndex - left;
        final int r = rightIndex - left;


        int i = getLongIndex(l);
        final int j = getLongIndex(r);







        final long start = LONG_MASK << l;
        final long end = LONG_MASK >>> -(r + 1);
        if (i == j) {


            data[i] |= start & end;
        } else {

            data[i] |= start;
            while (++i < j) {


                data[i] = -1L;
            }

            data[j] |= end;
        }
    }








    public int previousSetBit(int fromIndex) {
        return previousSetBitOrElse(fromIndex, -1);
    }










    int previousSetBitOrElse(int fromIndex, int defaultValue) {
        if (fromIndex < left) {

            return defaultValue;
        }
        final int index = fromIndex - left;
        int i = getLongIndex(index);

        long bits = data[i];


        if ((bits & getLongBit(index)) != 0) {
            return fromIndex;
        }



        bits &= LONG_MASK >>> -(index + 1);
        for (;;) {
            if (bits != 0) {




                return (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits) - 1 + left;
            }
            if (i == 0) {
                return defaultValue;
            }
            bits = data[--i];
        }
    }









    public int nextSetBit(int fromIndex) {
        return nextSetBitOrElse(fromIndex, -1);
    }










    int nextSetBitOrElse(int fromIndex, int defaultValue) {

        final int index = fromIndex < left ? 0 : fromIndex - left;

        int i = getLongIndex(index);



        long bits = data[i] & (LONG_MASK << index);
        for (;;) {
            if (bits != 0) {




                return i * Long.SIZE + Long.numberOfTrailingZeros(bits) + left;
            }
            if (++i == data.length) {
                return defaultValue;
            }
            bits = data[i];
        }
    }












    public int nextClearBit(int fromIndex) {
        if (fromIndex < left) {
            return fromIndex;
        }

        final int index = fromIndex - left;

        int i = getLongIndex(index);







        long bits = ~data[i] & (LONG_MASK << index);
        for (;;) {
            if (bits != 0) {
                return i * Long.SIZE + Long.numberOfTrailingZeros(bits) + left;
            }
            if (++i == data.length) {

                return data.length * Long.SIZE + left;
            }
            bits = ~data[i];
        }
    }












    public int previousClearBit(int fromIndex) {
        if (fromIndex < left) {

            return fromIndex;
        }
        final int index = fromIndex - left;
        int i = getLongIndex(index);







        long bits = ~data[i] & (LONG_MASK >>> -(index + 1));
        for (;;) {
            if (bits != 0) {




                return (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits) - 1 + left;
            }
            if (i == 0) {
                return left - 1;
            }
            bits = ~data[--i];
        }
    }






    public void forEach(IntConsumer action) {

        int wordIdx = left;
        for (int i = 0; i < data.length; i++) {
            long bits = data[i];
            int index = wordIdx;
            while (bits != 0) {
                if ((bits & 1L) == 1L) {
                    action.accept(index);
                }
                bits >>>= 1;
                index++;
            }
            wordIdx += Long.SIZE;
        }
    }













    public int toArray(int[] a) {


        int n = -1;
        int offset = left;
        for (long bits : data) {
            while (bits != 0) {
                final int index = Long.numberOfTrailingZeros(bits);
                a[++n] = index + offset;
                bits &= ~(1L << index);
            }
            offset += Long.SIZE;
        }
        return n + 1;
    }













    public int toArray2(int[] a) {

        int n = -1;
        for (int i = 0, offset = left; i < data.length; i++, offset += Long.SIZE) {
            long bits = data[i];
            int index = offset;
            while (bits != 0) {
                if ((bits & 1L) == 1L) {
                    a[++n] = index;
                }
                bits >>>= 1;
                index++;
            }
        }
        return n + 1;
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
    public boolean sparse() {

        return false;
    }

    @Override
    public boolean contains(int k) {

        return get(k);
    }

    @Override
    public void add(int index) {

        if (index < left) {
            lowerPivot = Math.max(index, lowerPivot);
        } else if (index > right) {
            upperPivot = Math.min(index, upperPivot);
        } else {
            set(index);
        }
    }

    @Override
    public void add(int fromIndex, int toIndex) {

        if (fromIndex == toIndex) {
            add(fromIndex);
            return;
        }






















        if (fromIndex <= right && toIndex >= left) {

            final int i = Math.max(fromIndex, left);
            final int j = Math.min(toIndex, right);
            set(i, j);
        } else if (toIndex < left) {
            lowerPivot = Math.max(toIndex, lowerPivot);
        } else {

            upperPivot = Math.min(fromIndex, upperPivot);
        }
    }

    @Override
    public int previousPivot(int k) {

        return previousSetBitOrElse(k, lowerPivot);
    }

    @Override
    public int nextPivotOrElse(int k, int other) {

        final int p = upperPivot == UPPER_DEFAULT ? other : upperPivot;
        return nextSetBitOrElse(k, p);
    }



    @Override
    public int previousIndex(int k) {



        final int index = k - left;
        int i = getLongIndex(index);



        long bits = data[i] & (LONG_MASK >>> -(index + 1));
        for (;;) {
            if (bits != 0) {




                return (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits) - 1 + left;
            }




            bits = data[--i];
        }
    }

    @Override
    public int nextIndex(int k) {



        final int index = k - left;
        int i = getLongIndex(index);



        long bits = data[i] & (LONG_MASK << index);
        for (;;) {
            if (bits != 0) {




                return i * Long.SIZE + Long.numberOfTrailingZeros(bits) + left;
            }




            bits = data[++i];
        }
    }









    @Override
    public int start() {
        return left();
    }

    @Override
    public int end() {
        return right();
    }

    @Override
    public int index(int i) {
        return i;
    }

    @Override
    public int previous(int i, int k) {
        return previousIndex(k);
    }

    @Override
    public int next(int i, int k) {
        return nextIndex(k);
    }































    ScanningPivotCache asScanningPivotCache(int lower, int upper) {
        return asScanningPivotCache(lower, upper, true);
    }












    static ScanningPivotCache createScanningPivotCache(int left, int right) {
        final IndexSet set = ofRange(left, right);
        return set.asScanningPivotCache(left, right, false);
    }












    private ScanningPivotCache asScanningPivotCache(int lower, int upper, boolean initialize) {
        if (initialize) {
            checkRange(lower, upper);
            final int capacity = data.length * Long.SIZE + lower;
            if (lower < left || upper >= capacity) {
                throw new IllegalArgumentException(
                    String.format("Unsupported range: [%d, %d] is not within [%d, %d]", lower, upper,
                        left, capacity - 1));
            }

            Arrays.fill(data, 0);
        }
        return new IndexPivotCache(lower, upper);
    }







    UpdatingInterval interval() {
        return new IndexSetUpdatingInterval(left, right);
    }








    private static void checkRange(int left, int right) {
        if (right < left) {
            throw new IllegalArgumentException(
                String.format("Invalid range: [%d, %d]", left, right));
        }
    }


















    private class IndexPivotCache implements ScanningPivotCache {

        private int left;

        private final int right;


        private int lowerPivot = -1;


        private int upperPivot = UPPER_DEFAULT;





        IndexPivotCache(int left, int right) {
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
        public boolean sparse() {

            return false;
        }

        @Override
        public boolean moveLeft(int newLeft) {
            if (newLeft > right) {

                return false;
            }
            if (newLeft < left) {
                throw new UnsupportedOperationException(
                    String.format("New left is outside current support: %d < %d", newLeft, left));
            }


            lowerPivot = previousPivot(newLeft);
            left = newLeft;
            return true;
        }

        @Override
        public boolean contains(int k) {

            return IndexSet.this.get(k);
        }

        @Override
        public int previousPivot(int k) {





            if (k < left) {
                return lowerPivot;
            }
            return Math.max(lowerPivot, IndexSet.this.previousSetBitOrElse(k, lowerPivot));
        }

        @Override
        public int nextPivotOrElse(int k, int other) {

            final int p = upperPivot == UPPER_DEFAULT ? other : upperPivot;
            return IndexSet.this.nextSetBitOrElse(k, p);
        }

        @Override
        public int nextNonPivot(int k) {

            return IndexSet.this.nextClearBit(k);
        }

        @Override
        public int previousNonPivot(int k) {

            return IndexSet.this.previousClearBit(k);
        }

        @Override
        public void add(int index) {

            if (index < left) {
                lowerPivot = Math.max(index, lowerPivot);
            } else if (index > right) {
                upperPivot = Math.min(index, upperPivot);
            } else {
                IndexSet.this.set(index);
            }
        }

        @Override
        public void add(int fromIndex, int toIndex) {
            if (fromIndex == toIndex) {
                add(fromIndex);
                return;
            }





















            if (fromIndex <= right && toIndex >= left) {

                final int i = Math.max(fromIndex, left);
                final int j = Math.min(toIndex, right);
                IndexSet.this.set(i, j);
            } else if (toIndex < left) {
                lowerPivot = Math.max(toIndex, lowerPivot);
            } else {

                upperPivot = Math.min(fromIndex, upperPivot);
            }
        }
    }







    private class IndexSetUpdatingInterval implements UpdatingInterval {

        private int left;

        private int right;





        IndexSetUpdatingInterval(int left, int right) {
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

            return left = nextIndex(k);
        }

        @Override
        public int updateRight(int k) {

            return right = previousIndex(k);
        }

        @Override
        public UpdatingInterval splitLeft(int ka, int kb) {

            final int lower = left;
            left = nextIndex(kb + 1);
            return new IndexSetUpdatingInterval(lower, previousIndex(ka - 1));
        }

        @Override
        public UpdatingInterval splitRight(int ka, int kb) {

            final int upper = right;
            right = previousIndex(ka - 1);
            return new IndexSetUpdatingInterval(nextIndex(kb + 1), upper);
        }
    }
}
