















package org.apache.commons.numbers.arrays;
















final class BitIndexUpdatingInterval implements UpdatingInterval {

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

        left = nextIndex(k);
        return left;
    }

    @Override
    public int updateRight(int k) {

        right = previousIndex(k);
        return right;
    }

    @Override
    public UpdatingInterval splitLeft(int ka, int kb) {

        final int lower = left;
        left = nextIndex(kb + 1);
        return new BitIndexUpdatingInterval(data, offset, lower, previousIndex(ka - 1));
    }
}
