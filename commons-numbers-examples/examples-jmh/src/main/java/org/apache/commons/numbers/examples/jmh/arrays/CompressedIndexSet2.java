















package org.apache.commons.numbers.examples.jmh.arrays;




















































final class CompressedIndexSet2 implements SearchableInterval {

    private static final long LONG_MASK = -1L;

    private static final int DIVIDE_BY_64 = 6;


    private final long[] data;


    private final int left;

    private final int right;







    private CompressedIndexSet2(int l, int r) {
        this.left = l;


        this.right = r;


        data = new long[getLongIndex((r - l) >>> 1) + 1];
    }














    static CompressedIndexSet2 ofRange(int left, int right) {
        checkLeft(left);
        checkRange(left, right);
        return new CompressedIndexSet2(left, right);
    }














    static CompressedIndexSet2 of(int[] indices) {
        return of(indices, indices.length);
    }












    static CompressedIndexSet2 of(int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        int min = indices[0];
        int max = min;
        for (int i = 0; ++i < n;) {
            min = Math.min(min, indices[i]);
            max = Math.max(max, indices[i]);
        }
        checkLeft(min);
        final CompressedIndexSet2 set = new CompressedIndexSet2(min, max);
        for (int i = -1; ++i < n;) {
            set.set(indices[i]);
        }
        return set;
    }








    private int compressIndex(int index) {
        return (index - left) >>> 1;
    }














    private static int getLongIndex(final int bitIndex) {






        return bitIndex >> DIVIDE_BY_64;
    }














    private static long getLongBit(final int bitIndex) {




        return 1L << bitIndex;
    }









    boolean get(int bitIndex) {

        final int index = compressIndex(bitIndex);
        final int i = getLongIndex(index);
        final long m = getLongBit(index);
        return (data[i] & m) != 0;
    }








    void set(int bitIndex) {

        final int index = compressIndex(bitIndex);
        final int i = getLongIndex(index);
        final long m = getLongBit(index);
        data[i] |= m;
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
    public int previousIndex(int k) {


        final int index = compressIndex(k);

        int i = getLongIndex(index);
        long bits = data[i];


        if ((bits & getLongBit(index)) != 0) {
            return k;
        }



        bits &= LONG_MASK >>> -(index + 1);
        for (;;) {
            if (bits != 0) {




                final int c = (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits);


                return (c << 1) - 1 + left;
            }




            bits = data[--i];
        }
    }

    @Override
    public int nextIndex(int k) {


        final int index = compressIndex(k);

        int i = getLongIndex(index);
        long bits = data[i];


        if ((bits & getLongBit(index)) != 0) {
            return k;
        }



        bits &= LONG_MASK << index;
        for (;;) {
            if (bits != 0) {




                final int c = i * Long.SIZE + Long.numberOfTrailingZeros(bits);


                return (c << 1) + left;
            }




            bits = data[++i];
        }
    }







    private static void checkLeft(int left) {
        if (left < 0) {
            throw new IllegalArgumentException("Invalid lower index: " + left);
        }
    }








    private static void checkRange(int left, int right) {
        if (right < left) {
            throw new IllegalArgumentException(
                String.format("Invalid range: [%d, %d]", left, right));
        }
    }
}
