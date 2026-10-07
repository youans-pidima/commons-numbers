
















package org.apache.commons.numbers.examples.jmh.arrays;






final class KeyUpdatingInterval implements UpdatingInterval, SplittingInterval {



    private static final int SCAN_SIZE = 256;


    private final int[] keys;

    private int l;

    private int r;








    KeyUpdatingInterval(int[] indices, int n) {
        this(indices, 0, n - 1);
    }






    private KeyUpdatingInterval(int[] indices, int l, int r) {
        keys = indices;
        this.l = l;
        this.r = r;
    }










    static KeyUpdatingInterval of(int[] indices, int n) {

        if (n <= 0) {
            throw new IllegalArgumentException("No indices to define the range");
        }
        int p = indices[0];
        for (int i = 0; ++i < n;) {
            final int c = indices[i];
            if (c <= p) {
                throw new IllegalArgumentException("Indices are not unique and ordered");
            }
            p = c;
        }
        if (indices[0] < 0) {
            throw new IllegalArgumentException("Unsupported min value: " + indices[0]);
        }
        if (indices[n - 1] == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Unsupported max value: " + Integer.MAX_VALUE);
        }
        return new KeyUpdatingInterval(indices, n);
    }

    @Override
    public int left() {
        return keys[l];
    }

    @Override
    public int right() {
        return keys[r];
    }

    @Override
    public int updateLeft(int k) {


        int i = l;
        do {
            ++i;
        } while (keys[i] < k);
        l = i;
        return keys[i];
    }

    @Override
    public int updateRight(int k) {


        int i = r;
        do {
            --i;
        } while (keys[i] > k);
        r = i;
        return keys[i];
    }

    @Override
    public UpdatingInterval splitLeft(int ka, int kb) {




        int i;
        if (r - l < SCAN_SIZE) {
            i = r;
            do {
                --i;
            } while (keys[i] > kb);
        } else {

            i = Partition.searchLessOrEqual(keys, l, r, kb);
        }
        final int lowerLeft = l;
        l = i + 1;



        while (keys[i] >= ka) {
            --i;
        }

        return new KeyUpdatingInterval(keys, lowerLeft, i);
    }

    @Override
    public UpdatingInterval splitRight(int ka, int kb) {




        int i;
        if (r - l < SCAN_SIZE) {
            i = r;
            do {
                --i;
            } while (keys[i] > kb);
        } else {

            i = Partition.searchLessOrEqual(keys, l, r, kb);
        }
        final int upperLeft = i + 1;



        while (keys[i] >= ka) {
            --i;
        }
        final int upperRight = r;
        r = i;

        return new KeyUpdatingInterval(keys, upperLeft, upperRight);
    }







    int size() {
        return r - l + 1;
    }

    @Override
    public boolean empty() {

        return r < 0;
    }

    @Override
    public SplittingInterval split(int ka, int kb) {
        if (ka <= left()) {

            if (kb >= right()) {

                invalidate();
            } else if (kb >= left()) {



                int i = l;
                do {
                    ++i;
                } while (keys[i] < kb);
                l = i;
            }
            return null;
        }
        if (kb >= right()) {




            int i = r;
            if (ka <= keys[i]) {
                do {
                    --i;
                } while (keys[i] > ka);
            }
            invalidate();
            return new KeyUpdatingInterval(keys, l, i);
        }

        return (SplittingInterval) splitLeft(ka, kb);
    }




    private void invalidate() {
        r = -1;
    }
}
