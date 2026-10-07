















package org.apache.commons.numbers.examples.jmh.arrays;













































final class CompressedIndexSet implements SearchableInterval, SearchableInterval2 {

    private static final long LONG_MASK = -1L;

    private static final int DIVIDE_BY_64 = 6;


    private final long[] data;


    private final int left;

    private final int right;

    private final int compression;








    private CompressedIndexSet(int compression, int l, int r) {
        this.compression = compression;
        this.left = l;


        this.right = r;


        data = new long[getLongIndex((r - l) >>> 1) + 1];
    }















    static CompressedIndexSet ofRange(int compression, int left, int right) {
        checkCompression(compression);
        checkLeft(left);
        checkRange(left, right);
        return new CompressedIndexSet(compression, left, right);
    }















    static CompressedIndexSet of(int compression, int[] indices) {
        return of(compression, indices, indices.length);
    }
















    static CompressedIndexSet of(int compression, int[] indices, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        checkCompression(compression);
        int min = indices[0];
        int max = min;
        for (int i = 0; ++i < n;) {
            min = Math.min(min, indices[i]);
            max = Math.max(max, indices[i]);
        }
        checkLeft(min);
        final CompressedIndexSet set = new CompressedIndexSet(compression, min, max);
        for (int i = -1; ++i < n;) {
            set.set(indices[i]);
        }
        return set;
    }











    static IndexIterator iterator(int compression, int[] indices, int n) {
        return of(compression, indices, n).new Iterator();
    }








    private int compressIndex(int index) {
        return (index - left) >>> compression;
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










    int previousIndexOrLeftMinus1(int k) {
        if (k < left) {

            return left - 1;
        }

        final int index = compressIndex(k > right ? right : k);

        int i = getLongIndex(index);
        long bits = data[i];


        if ((bits & getLongBit(index)) != 0) {
            return Math.min(k, right);
        }



        bits &= LONG_MASK >>> -(index + 1);
        for (;;) {
            if (bits != 0) {




                final int c = (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits);


                return (c << compression) - 1 + left;
            }
            if (i == 0) {
                return left - 1;
            }
            bits = data[--i];
        }
    }










    int nextIndexOrRightPlus1(int k) {
        if (k > right) {

            return right + 1;
        }

        final int index = compressIndex(k < left ? left : k);

        int i = getLongIndex(index);
        long bits = data[i];


        if ((bits & getLongBit(index)) != 0) {
            return Math.max(k, left);
        }



        bits &= LONG_MASK << index;
        for (;;) {
            if (bits != 0) {




                final int c = i * Long.SIZE + Long.numberOfTrailingZeros(bits);


                return (c << compression) + left;
            }
            if (++i == data.length) {
                return right + 1;
            }
            bits = data[i];
        }
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


                return (c << compression) - 1 + left;
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


                return (c << compression) + left;
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











    int previousClearBit(int k) {


        final int index = compressIndex(k);

        int i = getLongIndex(index);







        long bits = ~data[i] & (LONG_MASK >>> -(index + 1));
        for (;;) {
            if (bits != 0) {
                final int c = (i + 1) * Long.SIZE - Long.numberOfLeadingZeros(bits);
                return (c << compression) - 1 + left;
            }
            if (i == 0) {
                return left - 1;
            }
            bits = ~data[--i];
        }
    }












    int nextClearBit(int k) {


        final int index = compressIndex(k);

        int i = getLongIndex(index);







        long bits = ~data[i] & (LONG_MASK << index);
        for (;;) {
            if (bits != 0) {
                final int c = i * Long.SIZE + Long.numberOfTrailingZeros(bits);
                return (c << compression) + left;
            }
            if (++i == data.length) {

                return right + 1;
            }
            bits = ~data[i];
        }
    }







    private static void checkCompression(int compression) {
        if (!(compression > 0 && compression <= 31)) {
            throw new IllegalArgumentException("Invalid compression: " + compression);
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








    private class Iterator implements IndexIterator {

        private int l;

        private int r;

        private int nextL;




        Iterator() {
            l = CompressedIndexSet.this.left();
            r = nextClearBit(l) - 1;
            if (r < end()) {
                nextL = nextIndex(r + 1);
            } else {

                r = end();
            }
        }

        @Override
        public int left() {
            return l;
        }

        @Override
        public int right() {
            return r;
        }

        @Override
        public int end() {
            return CompressedIndexSet.this.right();
        }

        @Override
        public boolean next() {
            if (r < end()) {

                l = nextL;
                r = nextClearBit(l) - 1;
                if (r < end()) {
                    nextL = nextIndex(r + 1);
                } else {
                    r = end();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean positionAfter(int index) {

            if (r > index) {
                return true;
            }
            if (index < end()) {








                if (get(index + 1)) {


                    r = nextClearBit(index + 1) - 1;
                    if (r < end()) {
                        nextL = nextIndex(r + 1);
                    } else {
                        r = end();
                    }
                    l = index + 1;

                } else {


                    l = nextIndex(index + 1);
                    r = nextClearBit(l) - 1;
                    if (r < end()) {
                        nextL = nextIndex(r + 1);
                    } else {
                        r = end();
                    }
                }
                return true;
            }

            l = r = end();
            return false;
        }

        @Override
        public boolean nextAfter(int index) {
            if (r < end()) {
                return nextL > index;
            }

            return true;
        }
    }
}
