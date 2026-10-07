















package org.apache.commons.numbers.examples.jmh.arrays;
















final class BitIndexUpdatingInterval implements UpdatingInterval, SplittingInterval, IntervalAnalysis {

    private static final long LONG_MASK = -1L;

    private static final int DIVIDE_BY_64 = 6;


    private final long[] data;


    private final int offset;

    private int left;

    private int right;








    BitIndexUpdatingInterval(int left, int right) {
        this.offset = left;
        this.left = left;
        this.right = right;



        data = new long[getLongIndex(right - offset) + 1];
    }










    private BitIndexUpdatingInterval(long[] data, int offset, int left, int right) {
        this.data = data;
        this.offset = offset;
        this.left = left;
        this.right = right;
    }










    static BitIndexUpdatingInterval ofRange(int left, int right) {
        if (left < 0) {
            throw new IllegalArgumentException("Invalid lower index: " + left);
        }
        if (right == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid upper index: " + right);
        }
        if (right < left) {
            throw new IllegalArgumentException(
                String.format("Invalid range: [%d, %d]", left, right));
        }
        return new BitIndexUpdatingInterval(left, right);
    }










    static BitIndexUpdatingInterval of(int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        int min = indices[0];
        int max = min;
        for (int i = 1; i < n; i++) {
            min = Math.min(min, indices[i]);
            max = Math.max(max, indices[i]);
        }
        final BitIndexUpdatingInterval set = BitIndexUpdatingInterval.ofRange(min, max);
        for (int i = -1; ++i < n;) {
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








    void set(int bitIndex) {

        final int index = bitIndex - offset;
        final int i = getLongIndex(index);
        final long m = getLongBit(index);
        data[i] |= m;
    }











    private int nextIndex(int k) {


        final int index = k - offset;
        int i = getLongIndex(index);



        long bits = data[i] & (LONG_MASK << index);
        for (;;) {
            if (bits != 0) {




                return i * Long.SIZE + Long.numberOfTrailingZeros(bits) + offset;
            }



            bits = data[++i];
        }
    }











    private int previousIndex(int k) {


        final int index = k - offset;
        int i = getLongIndex(index);



        long bits = data[i] & (LONG_MASK >>> -(index + 1));
        for (;;) {
            if (bits != 0) {




                return (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits) - 1 + offset;
            }



            bits = data[--i];
        }
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
        return new BitIndexUpdatingInterval(data, offset, lower, previousIndex(ka - 1));
    }

    @Override
    public UpdatingInterval splitRight(int ka, int kb) {

        final int upper = right;
        right = previousIndex(ka - 1);
        return new BitIndexUpdatingInterval(data, offset, nextIndex(kb + 1), upper);
    }

    @Override
    public boolean empty() {

        return right < 0;
    }

    @Override
    public SplittingInterval split(int ka, int kb) {
        if (ka <= left) {

            if (kb >= right) {

                invalidate();
            } else if (kb >= left) {

                left = nextIndex(kb + 1);
            }
            return null;
        }
        if (kb >= right) {


            final int r = ka <= right ? previousIndex(ka - 1) : right;
            invalidate();
            return new BitIndexUpdatingInterval(data, offset, left, r);
        }

        return (SplittingInterval) splitLeft(ka, kb);
    }




    private void invalidate() {
        right = -1;
    }

    @Override
    public boolean saturated(int separation) {


        if (separation == 3) {
            return saturated3();
        }
        if (separation == 4) {
            return saturated4();
        }
        return false;
    }






    private boolean saturated3() {
        int c = 0;
        for (long x : data) {

            x = x | (x >>> 1);
            x = x | (x >>> 2);
            x = (x | (x >>> 4)) & 0b0000000100000001000000010000000100000001000000010000000100000001L;


            c += Long.bitCount(x);
        }

        return c << 3 >= right - left;
    }






    private boolean saturated4() {
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

        return c << 4 >= right - left;
    }
}
