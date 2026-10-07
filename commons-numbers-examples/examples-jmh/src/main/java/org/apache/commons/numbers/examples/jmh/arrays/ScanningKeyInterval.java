
















package org.apache.commons.numbers.examples.jmh.arrays;









final class ScanningKeyInterval implements SearchableInterval, SearchableInterval2 {






    private final int[] keys;

    private final int n;

    private final int i1;

    private final int i2;

    private final int i3;







    ScanningKeyInterval(int[] indices, int n) {
        keys = indices;
        this.n = n;

        i1 = n >>> 2;
        i2 = n >>> 1;
        i3 = i1 + i2;
    }










    static ScanningKeyInterval of(int[] indices, int n) {

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
        return new ScanningKeyInterval(indices, n);
    }

    @Override
    public int left() {
        return keys[0];
    }

    @Override
    public int right() {
        return keys[n - 1];
    }

    @Override
    public int previousIndex(int k) {
        return keys[previous(k)];
    }

    @Override
    public int nextIndex(int k) {
        return keys[next(k)];
    }

    @Override
    public int split(int ka, int kb, int[] upper) {
        int i = next(kb + 1);
        upper[0] = keys[i];

        do {
            --i;
        } while (keys[i] >= ka);
        return keys[i];
    }







    private int previous(int k) {





        int i;
        if (keys[i2] > k) {
            i = keys[i1] > k ? i1 : i2;
        } else {
            i = keys[i3] > k ? i3 : n;
        }
        do {
            --i;
        } while (keys[i] > k);
        return i;
    }







    private int next(int k) {





        int i;
        if (keys[i2] < k) {
            i = keys[i3] < k ? i3 : i2;
        } else {
            i = keys[i1] < k ? i1 : -1;
        }
        do {
            ++i;
        } while (keys[i] < k);
        return i;
    }

    @Override
    public int start() {
        return 0;
    }

    @Override
    public int end() {
        return n - 1;
    }

    @Override
    public int index(int i) {
        return keys[i];
    }

    @Override
    public int previous(int i, int k) {

        int j = i;
        do {
            --j;
        } while (keys[j] > k);
        return j;
    }

    @Override
    public int next(int i, int k) {

        int j = i;
        do {
            ++j;
        } while (keys[j] < k);
        return j;
    }

    @Override
    public int split(int lo, int hi, int ka, int kb, int[] upper) {





        int j = hi;
        do {
            --j;
        } while (keys[j] > kb);
        upper[0] = j + 1;

        while (keys[j] >= ka) {
            --j;
        }
        return j;
    }
}
