
















package org.apache.commons.numbers.examples.jmh.arrays;






final class KeyIndexIterator implements IndexIterator {

    private final int[] keys;

    private final int nm1;


    private int left;

    private int hi = -1;







    KeyIndexIterator(int[] indices, int n) {
        keys = indices;
        this.nm1 = n - 1;
        next();
    }










    static KeyIndexIterator of(int[] indices, int n) {

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
        return new KeyIndexIterator(indices, n);
    }

    @Override
    public int left() {
        return left;
    }

    @Override
    public int right() {
        return keys[hi];
    }

    @Override
    public int end() {
        return keys[nm1];
    }

    @Override
    public boolean next() {
        int i = hi;
        if (i < nm1) {

            int k = keys[++i];
            left = k;
            while (++i <= nm1 && k + 2 >= keys[i]) {
                k = keys[i];
            }
            hi = i - 1;
            return true;
        }
        return false;
    }

    @Override
    public boolean positionAfter(int index) {
        int i = hi;
        int r = keys[i];
        if (r <= index && i < nm1) {

            while (++i <= nm1) {
                r = keys[i];
                if (r > index) {

                    while (++i <= nm1 && r + 2 >= keys[i]) {
                        r = keys[i];
                    }
                    break;
                }
            }
            hi = --i;


            int k = r;
            while (--i >= 0 && keys[i] + 2 >= k) {
                k = keys[i];

                if (k <= index) {
                    left = index + 1;
                    return r > index;
                }
            }
            left = k;
        }
        return r > index;
    }

    @Override
    public boolean nextAfter(int index) {
        if (hi < nm1) {

            return keys[hi + 1] > index;
        }

        return true;
    }
}
