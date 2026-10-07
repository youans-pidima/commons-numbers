
















package org.apache.commons.numbers.arrays;






final class KeyUpdatingInterval implements UpdatingInterval {



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

            i = searchLessOrEqual(keys, l, r, kb);
        }
        final int lowerLeft = l;
        l = i + 1;



        while (keys[i] >= ka) {
            --i;
        }

        return new KeyUpdatingInterval(keys, lowerLeft, i);
    }






    int size() {
        return r - l + 1;
    }


















































    static int searchLessOrEqual(int[] a, int left, int right, int k) {
        int l = left;
        int r = right;
        while (l <= r) {

            final int m = (l + r) >>> 1;
            final int v = a[m];




            if (v < k) {
                l = m + 1;
            } else if (v > k) {
                r = m - 1;
            } else {

                return m;
            }
        }


        return r;
    }
}
