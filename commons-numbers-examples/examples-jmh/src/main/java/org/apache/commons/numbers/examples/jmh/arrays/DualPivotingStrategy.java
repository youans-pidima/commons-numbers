
















package org.apache.commons.numbers.examples.jmh.arrays;










enum DualPivotingStrategy {














    MEDIANS {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {

            final int len = right - left;


            final int third = Math.max(1, len / 3);
            final int m1 = left + third;
            final int m2 = right - third;

            if (data[m1] < data[m2]) {
                pivot2[0] = m2;
                return m1;
            }
            pivot2[0] = m1;
            return m2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int third = Math.max(1, len / 3);
            final int m1 = left + third;
            final int m2 = right - third;
            return new int[] {m1, m2};
        }

        @Override
        int samplingEffect() {
            return UNCHANGED;
        }
    },

















    SORT_5 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {



            final int len = right - left;
            final int sixth = 1 + (len >>> 3) + (len >>> 5) + (len >>> 6);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - sixth;
            final int p1 = p2 - sixth;
            final int p4 = p3 + sixth;
            final int p5 = p4 + sixth;
            Sorting.sort5(data, p1, p2, p3, p4, p5);
            pivot2[0] = p4;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
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
            return SORT;
        }
    },

















    SORT_5B {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {



            final int len = right - left;
            final int seventh = 1 + (len >>> 3) + (len >>> 6);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - seventh;
            final int p1 = p2 - seventh;
            final int p4 = p3 + seventh;
            final int p5 = p4 + seventh;
            Sorting.sort5(data, p1, p2, p3, p4, p5);
            pivot2[0] = p4;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
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
            return SORT;
        }
    },














    SORT_5B_SP {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {
            final int pivot1 = SORT_5B.pivotIndex(data, left, right, pivot2);
            if (data[pivot1] == data[pivot2[0]]) {



                pivot2[0] = pivot1;
            }
            return pivot1;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            return SORT_5B.getSampledIndices(left, right);
        }

        @Override
        int samplingEffect() {
            return SORT;
        }
    },

















    SORT_5C {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {



            final int len = right - left;
            final int eighth = 1 + (len >>> 3);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - eighth;
            final int p1 = p2 - eighth;
            final int p4 = p3 + eighth;
            final int p5 = p4 + eighth;
            Sorting.sort5(data, p1, p2, p3, p4, p5);
            pivot2[0] = p4;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int eighth = 1 + (len >>> 3);
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - eighth;
            final int p1 = p2 - eighth;
            final int p4 = p3 + eighth;
            final int p5 = p4 + eighth;
            return new int[] {p1, p2, p3, p4, p5};
        }

        @Override
        int samplingEffect() {
            return SORT;
        }
    },

































    SORT_5_OF_3 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {

            final int len = right - left;
            final int step = Math.max(1, len >>> 4);
            final int step3 = step * 3;
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - step3;
            final int p1 = p2 - step3;
            final int p4 = p3 + step3;
            final int p5 = p4 + step3;

            Sorting.sort3(data, p1 - step, p1, p1 + step);
            Sorting.sort3(data, p2 - step, p2, p2 + step);
            Sorting.sort3(data, p3 - step, p3, p3 + step);
            Sorting.sort3(data, p4 - step, p4, p4 + step);
            Sorting.sort3(data, p5 - step, p5, p5 + step);

            Sorting.sort5(data, p1, p2, p3, p4, p5);
            pivot2[0] = p4;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int step = Math.max(1, len >>> 4);
            final int step3 = step * 3;
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - step3;
            final int p1 = p2 - step3;
            final int p4 = p3 + step3;
            final int p5 = p4 + step3;
            return new int[] {
                p1 - step, p1, p1 + step,
                p2 - step, p2, p2 + step,
                p3 - step, p3, p3 + step,
                p4 - step, p4, p4 + step,
                p5 - step, p5, p5 + step,
            };
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },

































    SORT_4_OF_3 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {

            final int len = right - left;
            final int step = Math.max(1, (len >>> 4) + (len >>> 6));
            final int step3 = step * 3;
            final int p1 = left + (step << 1) - 1;
            final int p2 = p1 + step3;
            final int p3 = p2 + step3;
            final int p4 = p3 + step3;

            Sorting.sort3(data, p1 - step, p1, p1 + step);
            Sorting.sort3(data, p2 - step, p2, p2 + step);
            Sorting.sort3(data, p3 - step, p3, p3 + step);
            Sorting.sort3(data, p4 - step, p4, p4 + step);

            Sorting.sort4(data, p1, p2, p3, p4);
            pivot2[0] = p3;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int step = Math.max(1, (len >>> 4) + (len >>> 6));
            final int step3 = step * 3;
            final int p1 = left + (step << 1) - 1;
            final int p2 = p1 + step3;
            final int p3 = p2 + step3;
            final int p4 = p3 + step3;
            return new int[] {
                p1 - step, p1, p1 + step,
                p2 - step, p2, p2 + step,
                p3 - step, p3, p3 + step,
                p4 - step, p4, p4 + step,
            };
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },

































    SORT_3_OF_3 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {

            final int len = right - left;
            final int step = Math.max(1, len >>> 3);
            final int step3 = step * 3;
            final int p2 = left + (len >>> 1);
            final int p1 = p2 - step3;
            final int p3 = p2 + step3;

            Sorting.sort3(data, p1 - step, p1, p1 + step);
            Sorting.sort3(data, p2 - step, p2, p2 + step);
            Sorting.sort3(data, p3 - step, p3, p3 + step);

            Sorting.sort3(data, p1, p2, p3);
            pivot2[0] = p3;
            return p1;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int step = Math.max(1, len >>> 3);
            final int step3 = step * 3;
            final int p2 = left + (len >>> 1);
            final int p1 = p2 - step3;
            final int p3 = p2 + step3;
            return new int[] {
                p1 - step, p1, p1 + step,
                p2 - step, p2, p2 + step,
                p3 - step, p3, p3 + step,
            };
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },



































    SORT_5_OF_5 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {

            final int len = right - left;
            final int step = Math.max(1, len / 25);
            final int step2 = step << 1;
            final int step5 = step * 5;
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - step5;
            final int p1 = p2 - step5;
            final int p4 = p3 + step5;
            final int p5 = p4 + step5;

            Sorting.sort5(data, p1 - step2, p1 - step, p1, p1 + step, p1 + step2);
            Sorting.sort5(data, p2 - step2, p2 - step, p2, p2 + step, p2 + step2);
            Sorting.sort5(data, p3 - step2, p3 - step, p3, p3 + step, p3 + step2);
            Sorting.sort5(data, p4 - step2, p4 - step, p4, p4 + step, p4 + step2);
            Sorting.sort5(data, p5 - step2, p5 - step, p5, p5 + step, p5 + step2);

            Sorting.sort5(data, p1, p2, p3, p4, p5);
            pivot2[0] = p4;
            return p2;
        }

        @Override
        int[] getSampledIndices(int left, int right) {

            final int len = right - left;
            final int step = Math.max(1, len / 25);
            final int step2 = step << 1;
            final int step5 = step * 5;
            final int p3 = left + (len >>> 1);
            final int p2 = p3 - step5;
            final int p1 = p2 - step5;
            final int p4 = p3 + step5;
            final int p5 = p4 + step5;
            return new int[] {
                p1 - step2, p1 - step, p1, p1 + step, p1 + step2,
                p2 - step2, p2 - step, p2, p2 + step, p2 + step2,
                p3 - step2, p3 - step, p3, p3 + step, p3 + step2,
                p4 - step2, p4 - step, p4, p4 + step, p4 + step2,
                p5 - step2, p5 - step, p5, p5 + step, p5 + step2,
            };
        }

        @Override
        int samplingEffect() {
            return PARTIAL_SORT;
        }
    },


















    SORT_7 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {


            final int len = right - left;
            final int eighth = Math.max(1, len >>> 3);
            final int p4 = left + (len >>> 1);
            final int p3 = p4 - eighth;
            final int p2 = p3 - eighth;
            final int p1 = p2 - eighth;
            final int p5 = p4 + eighth;
            final int p6 = p5 + eighth;
            final int p7 = p6 + eighth;
            Sorting.sort7(data, p1, p2, p3, p4, p5, p6, p7);
            pivot2[0] = p5;
            return p3;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int eighth = Math.max(1, len >>> 3);
            final int p4 = left + (len >>> 1);
            final int p3 = p4 - eighth;
            final int p2 = p3 - eighth;
            final int p1 = p2 - eighth;
            final int p5 = p4 + eighth;
            final int p6 = p5 + eighth;
            final int p7 = p6 + eighth;
            return new int[] {p1, p2, p3, p4, p5, p6, p7};
        }

        @Override
        int samplingEffect() {
            return SORT;
        }
    },


















    SORT_8 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {



            final int len = right - left;
            final int ninth = Math.max(1, (len >>> 4) + (len >>> 5) + (len >>> 6));


            final int m = left + (len >>> 1);
            final int p4 = m - (ninth >> 1);
            final int p3 = p4 - ninth;
            final int p2 = p3 - ninth;
            final int p1 = p2 - ninth;
            final int p5 = m + (ninth >> 1) + 1;
            final int p6 = p5 + ninth;
            final int p7 = p6 + ninth;
            final int p8 = p7 + ninth;
            Sorting.sort8(data, p1, p2, p3, p4, p5, p6, p7, p8);
            pivot2[0] = p6;
            return p3;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int ninth = Math.max(1, (len >>> 4) + (len >>> 5) + (len >>> 6));
            final int m = left + (len >>> 1);
            final int p4 = m - (ninth >> 1);
            final int p3 = p4 - ninth;
            final int p2 = p3 - ninth;
            final int p1 = p2 - ninth;
            final int p5 = m + (ninth >> 1) + 1;
            final int p6 = p5 + ninth;
            final int p7 = p6 + ninth;
            final int p8 = p7 + ninth;
            return new int[] {p1, p2, p3, p4, p5, p6, p7, p8};
        }

        @Override
        int samplingEffect() {
            return SORT;
        }
    },

















    SORT_11 {
        @Override
        int pivotIndex(double[] data, int left, int right, int[] pivot2) {



            final int len = right - left;
            final int twelfth = Math.max(1, (len >>> 4) + (len >>> 6));
            final int p6 = left + (len >>> 1);
            final int p5 = p6 - twelfth;
            final int p4 = p5 - twelfth;
            final int p3 = p4 - twelfth;
            final int p2 = p3 - twelfth;
            final int p1 = p2 - twelfth;
            final int p7 = p6 + twelfth;
            final int p8 = p7 + twelfth;
            final int p9 = p8 + twelfth;
            final int p10 = p9 + twelfth;
            final int p11 = p10 + twelfth;
            Sorting.sort11(data, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11);
            pivot2[0] = p8;
            return p4;
        }

        @Override
        int[] getSampledIndices(int left, int right) {
            final int len = right - left;
            final int twelfth = Math.max(1, (len >>> 4) + (len >>> 6));
            final int p6 = left + (len >>> 1);
            final int p5 = p6 - twelfth;
            final int p4 = p5 - twelfth;
            final int p3 = p4 - twelfth;
            final int p2 = p3 - twelfth;
            final int p1 = p2 - twelfth;
            final int p7 = p6 + twelfth;
            final int p8 = p7 + twelfth;
            final int p9 = p8 + twelfth;
            final int p10 = p9 + twelfth;
            final int p11 = p10 + twelfth;
            return new int[] {p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11};
        }

        @Override
        int samplingEffect() {
            return SORT;
        }
    };


    static final int UNCHANGED = 0;

    static final int PARTIAL_SORT = 0x1;

    static final int SORT = 0x2;
















    abstract int pivotIndex(double[] data, int left, int right, int[] pivot2);










    abstract int[] getSampledIndices(int left, int right);











    abstract int samplingEffect();
}
