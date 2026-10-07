
















package org.apache.commons.numbers.examples.jmh.arrays;







final class BinarySearchKeyInterval implements SearchableInterval, SearchableInterval2 {

    private final int[] keys;

    private final int nm1;







    BinarySearchKeyInterval(int[] indices, int n) {
        nm1 = n - 1;
        keys = indices;
    }










    static BinarySearchKeyInterval of(int[] indices, int n) {

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
        return new BinarySearchKeyInterval(indices, n);
    }

    @Override
    public int left() {
        return keys[0];
    }

    @Override
    public int right() {
        return keys[nm1];
    }

    @Override
    public int previousIndex(int k) {


        return keys[Partition.searchLessOrEqual(keys, 0, nm1, k)];
    }

    @Override
    public int nextIndex(int k) {


        return keys[Partition.searchGreaterOrEqual(keys, 0, nm1, k)];
    }

    @Override
    public int split(int ka, int kb, int[] upper) {
        int i = Partition.searchGreaterOrEqual(keys, 0, nm1, kb + 1);
        upper[0] = keys[i];


        do {
            --i;
        } while (keys[i] >= ka);
        return keys[i];
    }

    @Override
    public int start() {
        return 0;
    }

    @Override
    public int end() {
        return nm1;
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





        int j = Partition.searchGreaterOrEqual(keys, lo, hi, kb + 1);
        upper[0] = j;


        do {
            --j;
        } while (keys[j] >= ka);
        return j;
    }
}
