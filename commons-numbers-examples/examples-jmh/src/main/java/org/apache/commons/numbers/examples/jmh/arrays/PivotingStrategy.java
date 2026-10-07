
















package org.apache.commons.numbers.examples.jmh.arrays;








enum PivotingStrategy {



    CENTRAL {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {
            return med(left, right);
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            return new int[] {med(left, right)};
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    },



    MEDIAN_OF_3 {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {
            return med3(data, left, med(left, right), right);
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            return new int[] {left, med(left, right), right};
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    },






    MEDIAN_OF_9 {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {
            final int s = (right - left) >>> 3;
            final int m = med(left, right);
            final int x = med3(data, left, left + s, left + (s << 1));
            final double a = data[x];
            final int y = med3(data, m - s, m, m + s);
            final double b = data[y];
            final int z = med3(data, right - (s << 1), right - s, right);
            return med3(a, b, data[z], x, y, z);
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            final int s = (right - left) >>> 3;
            final int m = med(left, right);
            return new int[] {
                left, left + s, left + (s << 1),
                m - s, m, m + s,
                right - (s << 1), right - s, right
            };
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    },







    DYNAMIC {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {
            if (right - left >= MED_9) {
                return MEDIAN_OF_9.pivotIndex(data, left, right, ignored);
            }
            return MEDIAN_OF_3.pivotIndex(data, left, right, ignored);
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            if (right - left >= MED_9) {
                return MEDIAN_OF_9.getSampledIndices(left, right, ignored);
            }
            return MEDIAN_OF_3.getSampledIndices(left, right, ignored);
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    },








    MEDIAN_OF_5 {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {



            final int len = right - left;
            final int sixth = 1 + (len >>> 3) + (len >>> 5) + (len >>> 6);


            final int p3 = left + (len >>> 1);
            final int p2 = p3 - sixth;
            final int p1 = p2 - sixth;
            final int p4 = p3 + sixth;
            final int p5 = p4 + sixth;
            return Sorting.median5(data, p1, p2, p3, p4, p5);
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            final int len = right - left;
            final int sixth = 1 + (len >>> 3) + (len >>> 5) + (len >>> 6);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - sixth;
            final int p1 = p2 - sixth;
            final int p4 = p3 + sixth;
            final int p5 = p4 + sixth;
            return new int[] {p1, p2, p3, p4, p5};
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },








    MEDIAN_OF_5B {
        @Override
        int pivotIndex(double[] data, int left, int right, int ignored) {



            final int len = right - left;
            final int seventh = 1 + (len >>> 3) + (len >>> 6);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - seventh;
            final int p1 = p2 - seventh;
            final int p4 = p3 + seventh;
            final int p5 = p4 + seventh;
            Sorting.sort4(data, p1, p2, p4, p5);

            if (data[p3] < data[p2]) {
                return p2;
            }
            return data[p3] > data[p4] ? p4 : p3;
        }

        @Override
        int[] getSampledIndices(int left, int right, int ignored) {
            final int len = right - left;
            final int seventh = 1 + (len >>> 3) + (len >>> 6);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - seventh;
            final int p1 = p2 - seventh;
            final int p4 = p3 + seventh;
            final int p5 = p4 + seventh;
            return new int[] {p1, p2, p3, p4, p5};
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },



    TARGET {
        @Override
        int pivotIndex(double[] data, int left, int right, int k) {
            return k;
        }

        @Override
        int[] getSampledIndices(int left, int right, int k) {
            return new int[] {k};
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    };


    static final int UNCHANGED = 0;

    static final int PARTIAL_SORT = 0x1;

    static final int SORT = 0x2;

    private static final int MED_9 = 40;















    private static int med(int left, int right) {
        return (left + right + 1) >>> 1;
    }










    private static int med3(double[] data, int i, int j, int k) {
        return med3(data[i], data[j], data[k], i, j, k);
    }












    private static int med3(double a, double b, double c, int ia, int ib, int ic) {
        if (a < b) {
            if (b < c) {
                return ib;
            }
            return a < c ? ic : ia;
        }
        if (b > c) {
            return ib;
        }
        return a > c ? ic : ia;
    }


















    abstract int pivotIndex(double[] data, int left, int right, int k);











    abstract int[] getSampledIndices(int left, int right, int k);











    abstract int samplingEffect();
}
