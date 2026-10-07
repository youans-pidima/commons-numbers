
















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;
import java.util.BitSet;




























class KthSelector {

    static final int[] NO_PIVOTS = {};


    private static final int MIN_SELECT_SIZE = 17;


    private final PivotingStrategy pivotingStrategy;


    private final int minSelectSize;





    KthSelector() {
        this(PivotingStrategy.MEDIAN_OF_3);
    }






    KthSelector(PivotingStrategy pivotingStrategy) {
        this(pivotingStrategy, MIN_SELECT_SIZE);
    }







    KthSelector(PivotingStrategy pivotingStrategy, int minSelectSize) {
        this.pivotingStrategy = pivotingStrategy;
        this.minSelectSize = minSelectSize;
    }













    double selectSP(double[] data, int k, double[] kp1) {
        int begin = 0;
        int end = data.length;
        while (end - begin > minSelectSize) {

            final int pivot = partitionSP(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k));
            if (k == pivot) {

                return finalSelection(data, k, kp1, end);
            } else if (k < pivot) {

                end = pivot;
            } else {

                begin = pivot + 1;
            }
        }
        sortRange(data, begin, end);
        if (kp1 != null) {


            kp1[0] = data[k + 1];
        }
        return data[k];
    }














    double selectSPN(double[] data, int k, double[] kp1) {

        final int length = sortNaN(data);
        if (k >= length) {
            if (kp1 != null) {
                kp1[0] = Double.NaN;
            }
            return Double.NaN;
        }

        int begin = 0;
        int end = length;
        while (end - begin > minSelectSize) {

            final int pivot = partitionSPN(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k));
            if (k == pivot) {

                if (data[k] == 0) {
                    orderSignedZeros(data, 0, length);
                }
                return finalSelection(data, k, kp1, end);
            } else if (k < pivot) {

                end = pivot;
            } else {

                begin = pivot + 1;
            }
        }
        insertionSort(data, begin, end, begin != 0);
        if (data[k] == 0) {
            orderSignedZeros(data, 0, length);
        }
        if (kp1 != null) {


            kp1[0] = data[k + 1];
        }
        return data[k];
    }

















    double selectSPH(double[] data, int[] pivotsHeap, int k, double[] kp1) {
        final int heapLength = pivotsHeap.length;
        if (heapLength == 0) {

            return selectSP(data, k, kp1);
        }
        int begin = 0;
        int end = data.length;
        int node = 0;
        while (end - begin > minSelectSize) {
            int pivot;

            if (node < heapLength && pivotsHeap[node] >= 0) {


                pivot = pivotsHeap[node];
            } else {

                pivot = partitionSP(data, begin, end,
                    pivotingStrategy.pivotIndex(data, begin, end - 1, k));
                if (node < heapLength) {
                    pivotsHeap[node] = pivot;
                }
            }

            if (k == pivot) {

                return finalSelection(data, k, kp1, end);
            } else if (k < pivot) {

                end = pivot;
                if (node < heapLength) {
                    node = Math.min((node << 1) + 1, heapLength);
                }
            } else {

                begin = pivot + 1;
                if (node < heapLength) {
                    node = Math.min((node << 1) + 2, heapLength);
                }
            }
        }
        sortRange(data, begin, end);
        if (kp1 != null) {


            kp1[0] = data[k + 1];
        }
        return data[k];
    }















    private static double finalSelection(double[] data, int k, double[] kp1, int end) {
        if (kp1 != null) {



            final int limit = Math.min(end, data.length - 1);
            double min = data[k + 1];
            for (int i = k + 2; i <= limit; i++) {
                if (DoubleMath.lessThan(data[i], min)) {
                    min = data[i];
                }
            }
            kp1[0] = min;
        }
        return data[k];
    }















    void partitionSP(double[] data, int... k) {
        final int n = k.length;
        if (n <= 1) {
            if (n == 1) {
                selectSP(data, k[0], null);
            }
            return;
        }

        final int length = data.length;
        final BitSet pivots = new BitSet(length);

        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionSP(data, begin, end, pivots, kk);
        }
    }















    private void partitionSP(double[] data, int begin, int end, BitSet pivots, int k) {

        while (end - begin > minSelectSize) {

            final int pivot = partitionSP(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k));
            pivots.set(pivot);
            if (k == pivot) {

                return;
            } else if (k < pivot) {

                end = pivot;
            } else {

                begin = pivot + 1;
            }
        }
        setPivots(begin, end, pivots);
        sortRange(data, begin, end);
    }














    private static int partitionSP(double[] data, int begin, int end, int pivot) {
        final double value = data[pivot];
        data[pivot] = data[begin];

        int i = begin + 1;
        int j = end - 1;
        while (i < j) {
            while (i < j && DoubleMath.greaterThan(data[j], value)) {
                --j;
            }
            while (i < j && DoubleMath.lessThan(data[i], value)) {
                ++i;
            }
            if (i < j) {
                final double tmp = data[i];
                data[i++] = data[j];
                data[j--] = tmp;
            }
        }

        if (i >= end || DoubleMath.greaterThan(data[i], value)) {
            --i;
        }
        data[begin] = data[i];
        data[i] = value;
        return i;
    }















    void partitionSPN(double[] data, int... k) {
        final int n = k.length;
        if (n <= 1) {
            if (n == 1) {
                selectSPN(data, k[0], null);
            }
            return;
        }



        final int length = sortNaN(data);
        if (length < 1) {
            return;
        }

        final BitSet pivots = new BitSet(length);


        boolean zeros = false;
        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (kk >= length || pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionSPN(data, begin, end, pivots, kk);
            zeros = zeros || data[kk] == 0;
        }


        if (zeros) {
            orderSignedZeros(data, 0, length);
        }
    }


















    private void partitionSPN(double[] data, int begin, int end, BitSet pivots, int k) {

        while (end - begin > minSelectSize) {

            final int pivot = partitionSPN(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k));
            pivots.set(pivot);
            if (k == pivot) {

                return;
            } else if (k < pivot) {

                end = pivot;
            } else {

                begin = pivot + 1;
            }
        }
        setPivots(begin, end, pivots);
        sortRange(data, begin, end);
    }

















    private static int partitionSPN(double[] data, int begin, int end, int pivot) {
        final double value = data[pivot];
        data[pivot] = data[begin];

        int i = begin + 1;
        int j = end - 1;
        while (i < j) {
            while (i < j && data[j] > value) {
                --j;
            }
            while (i < j && data[i] < value) {
                ++i;
            }
            if (i < j) {
                final double tmp = data[i];
                data[i++] = data[j];
                data[j--] = tmp;
            }
        }

        if (i >= end || data[i] > value) {
            --i;
        }
        data[begin] = data[i];
        data[i] = value;
        return i;
    }








    private static void sortRange(double[] data, int begin, int end) {
        Arrays.sort(data, begin, end);
    }

















    private static void insertionSort(double[] data, int begin, int end, boolean internal) {
        Sorting.sort(data, begin, end - 1, internal);
    }









    private static int sortNaN(double[] data) {
        int end = data.length;

        while (--end > 0) {
            if (!Double.isNaN(data[end])) {
                break;
            }
        }
        end++;
        for (int i = end; i > 0;) {
            final double v = data[--i];
            if (Double.isNaN(v)) {
                data[i] = data[--end];
                data[end] = v;
            }
        }
        return end;
    }












    private static void fixSignedZeros(double[] data, int begin, int end) {
        int j;
        if (data[begin] <= 0 && data[end - 1] >= 0) {
            int i = begin;
            while (data[i] < 0) {
                i++;
            }
            j = end - 1;
            while (data[j] > 0) {
                j--;
            }
            sortZero(data, i, j + 1);
        }
    }









    private static void orderSignedZeros(double[] data, int begin, int end) {
        int c = countSignedZeros(data, begin, end);
        if (c != 0) {
            int i = begin - 1;
            while (++i < end) {
                if (data[i] == 0) {
                    data[i] = -0.0;
                    if (--c == 0) {
                        break;
                    }
                }
            }
            while (++i < end) {
                if (data[i] == 0) {
                    data[i] = 0.0;
                }
            }
        }
    }









    static int countSignedZeros(double[] data, int begin, int end) {

        int c = 0;
        for (int i = begin; i < end; i++) {
            if (data[i] == 0 && Double.doubleToRawLongBits(data[i]) < 0) {
                c++;
            }
        }
        return c;
    }









    static void sortZero(double[] data, int begin, int end) {

        int c = 0;
        for (int i = begin; i < end; i++) {
            if (Double.doubleToRawLongBits(data[i]) < 0) {
                c++;
            }
        }

        if (c != 0) {
            int i = begin;
            while (c-- > 0) {
                data[i++] = -0.0;
            }
            while (i < end) {
                data[i++] = 0.0;
            }
        }
    }








    private static void setPivots(int from, int to, BitSet pivots) {
        if (from + 1 == to) {
            pivots.set(from);
        } else {
            pivots.set(from, to);
        }
    }















    void partitionSBM(double[] data, int... k) {
        final int n = k.length;
        if (n < 1) {
            return;
        }


        final int length = sortNaN(data);
        if (length < 1) {
            return;
        }

        if (n == 1) {
            if (k[0] < length) {
                partitionSBM(data, 0, length, k[0]);
            }
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {
            if (k[0] < length) {
                final int p = partitionSBM(data, 0, length, k[0]);
                if (p > k[1]) {
                    partitionMin(data, k[1], p);
                }
            }
            return;
        }




















        final BitSet pivots = new BitSet(length);

        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (kk >= length || pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionSBM(data, begin, end, pivots, kk);
        }
    }











    static void partitionMin(double[] data, int begin, int end) {
        int i = begin;
        double min = data[i];
        int j = i;
        while (++i < end) {
            if (data[i] < min) {
                min = data[i];
                j = i;
            }
        }

        data[j] = data[begin];
        data[begin] = min;
    }











    static void partitionMax(double[] data, int begin, int end) {
        int i = end - 1;
        double max = data[i];
        int j = i;
        while (--i >= begin) {
            if (data[i] > max) {
                max = data[i];
                j = i;
            }
        }

        data[j] = data[end - 1];
        data[end - 1] = max;
    }

















    private void partitionSBM(double[] data, int begin, int end, BitSet pivots, int k) {

        final int[] upper = {0};
        while (end - begin > minSelectSize) {

            final int from = partitionSBM(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k), upper);
            final int to = upper[0];
            setPivots(from, to, pivots);
            if (k >= to) {

                begin = to;
            } else if (k < from) {

                end = from;
            } else {

                return;
            }
        }
        setPivots(begin, end, pivots);
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);
    }

















    private static int partitionSBM(double[] data, int begin, int end, int pivot, int[] upper) {

























        final int l = begin;
        final int r = end - 1;

        int p = l;
        int q = r;


        final double v = data[pivot];
        data[pivot] = data[r];
        data[r] = v;


        int c = 0;
        if (v == 0) {
            c = countSignedZeros(data, begin, end);
        }


        while (data[p] == v) {
            if (++p == q) {

                if (c != 0) {
                    sortZero(data, begin, end);
                }
                upper[0] = end;
                return begin;
            }
        }

        while (data[q - 1] == v) {
            q--;
        }

        int i = p - 1;
        int j = q;

        for (;;) {
            do {
                ++i;
            } while (data[i] < v);
            while (v < data[--j]) {

                if (j == l) {
                    break;
                }
            }
            if (i >= j) {



                if (i == j && v == data[i]) {


                    data[i++] = data[p];
                    data[p++] = v;
                }
                break;
            }

            final double vj = data[i];
            final double vi = data[j];
            data[i] = vi;
            data[j] = vj;
            if (vi == v) {


                data[i] = data[p];
                data[p++] = v;
            }
            if (vj == v) {

                data[j] = data[--q];
                data[q] = v;
            }
        }




        data[r] = data[i];
        data[i] = v;



        j = i++;









        int move = Math.min(p - l, j - p);
        final int lower = j - (p - l);
        for (int k = l; move-- > 0; k++) {
            data[k] = data[--j];
            data[j] = v;
        }

        move = Math.min(r - q, q - i);
        upper[0] = i + (r - q);
        for (int k = r; move-- > 0; i++) {
            data[--k] = data[i];
            data[i] = v;
        }


        if (c != 0) {
            p = lower;
            while (c-- > 0) {
                data[p++] = -0.0;
            }
            while (p < upper[0]) {
                data[p++] = 0.0;
            }
        }

        return lower;
    }





















    private int partitionSBM(double[] data, int begin, int end, int k) {

        final int[] upper = {0};
        while (end - begin > minSelectSize) {

            final int from = partitionSBM(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k), upper);
            final int to = upper[0];
            if (k >= to) {

                begin = to;
            } else if (k < from) {

                end = from;
            } else {

                return end;
            }
        }
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);


        return begin;
    }















    void partitionBM(double[] data, int... k) {
        final int n = k.length;
        if (n < 1) {
            return;
        }


        final int length = sortNaN(data);
        if (length < 1) {
            return;
        }

        if (n == 1) {
            partitionBM(data, 0, length, k[0]);
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {
            final int p = partitionBM(data, 0, length, k[0]);
            if (p > k[1]) {
                partitionMin(data, k[1], p);
            }
            return;
        }


        final BitSet pivots = new BitSet(length);

        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (kk >= length || pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionBM(data, begin, end, pivots, kk);
        }
    }

















    private void partitionBM(double[] data, int begin, int end, BitSet pivots, int k) {

        final int[] upper = {0};
        while (end - begin > minSelectSize) {

            final int from = partitionBM(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k), upper);
            final int to = upper[0];
            setPivots(from, to, pivots);
            if (k >= to) {

                begin = to;
            } else if (k < from) {

                end = from;
            } else {

                return;
            }
        }
        setPivots(begin, end, pivots);
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);
    }

















    private static int partitionBM(double[] data, int begin, int end, int pivot, int[] upper) {






















        final int l = begin;
        final int r = end - 1;

        int i = l;
        int j = r;
        int p = l;
        int q = r;

        final double v = data[pivot];


        int c = 0;
        if (v == 0) {
            c = countSignedZeros(data, begin, end);
        }

        for (;;) {
            while (i <= j && data[i] <= v) {
                if (data[i] == v) {

                    data[i] = data[p];
                    data[p++] = v;
                }
                i++;
            }
            while (j >= i && data[j] >= v) {
                if (v == data[j]) {

                    data[j] = data[q];
                    data[q--] = v;
                }
                j--;
            }
            if (i > j) {
                break;
            }
            swap(data, i++, j--);
        }


        int s = Math.min(p - l, i - p);
        for (int k = l; s > 0; k++, s--) {

            data[k] = data[i - s];
            data[i - s] = v;
        }
        s = Math.min(q - j, r - q);
        for (int k = i; s > 0; k++, s--) {

            data[end - s] = data[k];
            data[k] = v;
        }


        i = i - p + l;
        j = j - q + end;
        upper[0] = j;


        if (c != 0) {
            p = i;
            while (c-- > 0) {
                data[p++] = -0.0;
            }
            while (p < j) {
                data[p++] = 0.0;
            }
        }

        return i;
    }





















    private int partitionBM(double[] data, int begin, int end, int k) {

        final int[] upper = {0};
        while (end - begin > minSelectSize) {

            final int from = partitionBM(data, begin, end,
                pivotingStrategy.pivotIndex(data, begin, end - 1, k), upper);
            final int to = upper[0];
            if (k >= to) {

                begin = to;
            } else if (k < from) {

                end = from;
            } else {

                return end;
            }
        }
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);


        return begin;
    }















    void partitionDP(double[] data, int... k) {
        final int n = k.length;
        if (n < 1) {
            return;
        }


        final int length = sortNaN(data);
        if (length < 1) {
            return;
        }

        if (n == 1) {
            partitionDP(data, 0, length, (BitSet) null, k[0]);
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {
            final int p = partitionDP(data, 0, length, (BitSet) null, k[0]);
            if (p > k[1]) {
                partitionMin(data, k[1], p);
            }
            return;
        }


        final BitSet pivots = new BitSet(length);

        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (kk >= length || pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionDP(data, begin, end, pivots, kk);
        }
    }






















    private int partitionDP(double[] data, int begin, int end, BitSet pivots, int k) {

        final int[] bounds = new int[4];
        int div = 3;
        while (end - begin > minSelectSize) {
            div = partitionDP(data, begin, end, bounds, div);
            final int k0 = bounds[0];
            final int k1 = bounds[1];
            final int k2 = bounds[2];
            final int k3 = bounds[3];

            if (pivots != null) {
                setPivots(k0, k1, pivots);
                setPivots(k2 + 1, k3 + 1, pivots);
            }
            if (k > k3) {

                begin = k3 + 1;
            } else if (k < k0) {

                end = k0;
            } else if (k >= k1 && k <= k2) {

                begin = k1;
                end = k2 + 1;
            } else {


                if (k + 1 < k1) {
                    return k0;
                }
                if (k + 1 < k3) {
                    return k2 + 1;
                }
                return end;
            }
        }
        if (pivots != null) {
            setPivots(begin, end, pivots);
        }
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);


        return begin;
    }































    private int partitionDP(double[] a, int left, int end, int[] bounds, int div) {
























        final int right = end - 1;
        final int len = right - left;




        final int third = len / div;
        int m1 = left + third;
        int m2 = right - third;
        if (m1 <= left) {
            m1 = left + 1;
        }
        if (m2 >= right) {
            m2 = right - 1;
        }
        if (a[m1] < a[m2]) {
            swap(a, m1, left);
            swap(a, m2, right);
        } else {
            swap(a, m1, right);
            swap(a, m2, left);
        }

        final double pivot1 = a[left];
        final double pivot2 = a[right];


        if (pivot1 == pivot2) {
            final int lower = partitionSBM(a, left, end, m1, bounds);
            final int upper = bounds[0];

            bounds[0] = lower;
            bounds[3] = upper - 1;

            bounds[1] = upper;
            bounds[2] = upper - 1;
            return div;
        }


        int c = 0;
        if (pivot1 == 0 || pivot2 == 0) {
            c = countSignedZeros(a, left, end);
        }


        int less = left + 1;
        int great = right - 1;


        while (a[less] < pivot1) {
            less++;
        }
        while (a[great] > pivot2) {
            great--;
        }


        SORTING:
        for (int k = less; k <= great; k++) {
            final double v = a[k];
            if (v < pivot1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > pivot2) {




                while (a[great] > pivot2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final double w = a[great];
                a[great] = v;
                great--;

                if (w < pivot1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }

        final int dist = great - less;





        if (dist < (minSelectSize >>> 1)) {
            div++;
        }


        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;




        bounds[0] = less - 1;
        bounds[3] = great + 1;









        if ((dist > len - (minSelectSize >>> 1) || c != 0) && pivot1 != pivot2) {

            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }

            EQUAL:
            for (int k = less; k <= great; k++) {
                final double v = a[k];
                if (v == pivot1) {

                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == pivot2) {
                    while (a[great] == pivot2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final double w = a[great];
                    a[great] = v;
                    great--;
                    if (w == pivot1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }
        }

        if (pivot1 < pivot2 && less < great) {

            bounds[1] = less;
            bounds[2] = great;
        } else {

            bounds[1] = bounds[3] + 1;
            bounds[2] = bounds[3];
        }


        if (c != 0) {
            int i;
            if (pivot1 == 0) {
                i = bounds[0];
                while (c-- > 0) {
                    a[i++] = -0.0;
                }
                while (i < end && a[i] == 0) {
                    a[i++] = 0.0;
                }
            } else {
                i = bounds[3];
                while (a[i] == 0) {
                    a[i--] = 0.0;
                    if (i == left) {
                        break;
                    }
                }
                while (c-- > 0) {
                    a[++i] = -0.0;
                }
            }
        }

        return div;
    }















    void partitionDP5(double[] data, int... k) {
        final int n = k.length;
        if (n < 1) {
            return;
        }


        final int length = sortNaN(data);
        if (length < 1) {
            return;
        }

        if (n == 1) {
            partitionDP5(data, 0, length, (BitSet) null, k[0]);
            return;
        }

        if (n == 2 && k[0] + 1 == k[1]) {
            final int p = partitionDP5(data, 0, length, (BitSet) null, k[0]);
            if (p > k[1]) {
                partitionMin(data, k[1], p);
            }
            return;
        }


        final BitSet pivots = new BitSet(length);

        for (int i = 0; i < n; i++) {
            final int kk = k[i];
            if (kk >= length || pivots.get(kk)) {

                continue;
            }
            int begin;
            int end;
            if (i == 0) {
                begin = 0;
                end = length;
            } else {

                begin = pivots.previousSetBit(kk) + 1;
                end = pivots.nextSetBit(kk + 1);
                if (end < 0) {
                    end = length;
                }
            }
            partitionDP5(data, begin, end, pivots, kk);
        }
    }






















    private int partitionDP5(double[] data, int begin, int end, BitSet pivots, int k) {

        final int[] bounds = new int[4];
        while (end - begin > minSelectSize) {
            partitionDP5(data, begin, end, bounds);
            final int k0 = bounds[0];
            final int k1 = bounds[1];
            final int k2 = bounds[2];
            final int k3 = bounds[3];

            if (pivots != null) {
                setPivots(k0, k1, pivots);
                setPivots(k2 + 1, k3 + 1, pivots);
            }
            if (k > k3) {

                begin = k3 + 1;
            } else if (k < k0) {

                end = k0;
            } else if (k >= k1 && k <= k2) {

                begin = k1;
                end = k2 + 1;
            } else {


                if (k + 1 < k1) {
                    return k0;
                }
                if (k + 1 < k3) {
                    return k2 + 1;
                }
                return end;
            }
        }
        if (pivots != null) {
            setPivots(begin, end, pivots);
        }
        insertionSort(data, begin, end, begin != 0);
        fixSignedZeros(data, begin, end);


        return begin;
    }





























    private static void partitionDP5(double[] a, int left, int end, int[] bounds) {















        final int right = end - 1;
        final int len = right - left;







        final int sixth = 1 + (len >>> 3) + (len >>> 5);
        final int p3 = left + (len >>> 1);
        final int p2 = p3 - sixth;
        final int p1 = p2 - sixth;
        final int p4 = p3 + sixth;
        final int p5 = p4 + sixth;
        Sorting.sort5(a, p1, p2, p3, p4, p5);





        final double pivot1 = a[p2];
        final double pivot2 = a[p4];



        if (pivot1 == pivot2) {


            final int lower = partitionSBM(a, left, end, p3, bounds);
            final int upper = bounds[0];

            bounds[0] = lower;
            bounds[3] = upper - 1;

            bounds[1] = upper;
            bounds[2] = upper - 1;
            return;
        }


        int c = 0;
        if (pivot1 == 0 || pivot2 == 0) {
            c = countSignedZeros(a, left, end);
        }



        a[p2] = a[left];
        a[p4] = a[right];





        int less = left + 1;
        int great = right - 1;


        while (a[less] < pivot1) {
            less++;
        }
        while (a[great] > pivot2) {
            great--;
        }


        SORTING:
        for (int k = less; k <= great; k++) {
            final double v = a[k];
            if (v < pivot1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > pivot2) {




                while (a[great] > pivot2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final double w = a[great];
                a[great] = v;
                great--;

                if (w < pivot1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }



        a[left] = a[less - 1];
        a[less - 1] = pivot1;
        a[right] = a[great + 1];
        a[great + 1] = pivot2;




        bounds[0] = less - 1;
        bounds[3] = great + 1;











        if ((less < p1 && great > p5 || c != 0) && pivot1 != pivot2) {


            while (a[less] == pivot1) {
                less++;
            }
            while (a[great] == pivot2) {
                great--;
            }


            EQUAL:
            for (int k = less; k <= great; k++) {
                final double v = a[k];
                if (v == pivot1) {

                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == pivot2) {
                    while (a[great] == pivot2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final double w = a[great];
                    a[great] = v;
                    great--;
                    if (w == pivot1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }
        }

        if (pivot1 < pivot2 && less < great) {

            bounds[1] = less;
            bounds[2] = great;
        } else {

            bounds[1] = bounds[3] + 1;
            bounds[2] = bounds[3];
        }


        if (c != 0) {
            int i;
            if (pivot1 == 0) {
                i = bounds[0];
                while (c-- > 0) {
                    a[i++] = -0.0;
                }
                while (i < end && a[i] == 0) {
                    a[i++] = 0.0;
                }
            } else {
                i = bounds[3];
                while (a[i] == 0) {
                    a[i--] = 0.0;
                    if (i == left) {
                        break;
                    }
                }
                while (c-- > 0) {
                    a[++i] = -0.0;
                }
            }
        }
    }








    private static void swap(double[] array, int i, int j) {
        final double tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }








    void sortSP(double[] data) {
        sortSP(data, 0, data.length);
    }








    private void sortSP(double[] data, int begin, int end) {
        if (end - begin <= 1) {
            return;
        }
        final int i = partitionSP(data, begin, end,
            pivotingStrategy.pivotIndex(data, begin, end - 1, begin));
        sortSP(data, begin, i);
        sortSP(data, i + 1, end);
    }








    void sortSBM(double[] data) {
        sortSBM(data, 0, sortNaN(data));
    }








    private void sortSBM(double[] data, int begin, int end) {
        if (end - begin <= minSelectSize) {
            insertionSort(data, begin, end, begin != 0);
            if (begin < end) {
                fixSignedZeros(data, begin, end);
            }
            return;
        }
        final int[] to = {0};
        final int from = partitionSBM(data, begin, end,
            pivotingStrategy.pivotIndex(data, begin, end - 1, begin), to);
        sortSBM(data, begin, from);
        sortSBM(data, to[0], end);
    }








    void sortBM(double[] data) {
        sortBM(data, 0, sortNaN(data));
    }








    private void sortBM(double[] data, int begin, int end) {
        if (end - begin <= minSelectSize) {
            insertionSort(data, begin, end, begin != 0);
            if (begin < end) {
                fixSignedZeros(data, begin, end);
            }
            return;
        }
        final int[] to = {0};
        final int from = partitionBM(data, begin, end,
            pivotingStrategy.pivotIndex(data, begin, end - 1, begin), to);
        sortBM(data, begin, from);
        sortBM(data, to[0], end);
    }








    void sortDP(double[] data) {
        sortDP(data, 0, sortNaN(data), 3);
    }









    private void sortDP(double[] data, int begin, int end, int div) {
        if (end - begin <= minSelectSize) {
            insertionSort(data, begin, end, begin != 0);
            if (begin < end) {
                fixSignedZeros(data, begin, end);
            }
            return;
        }
        final int[] bounds = new int[4];
        div = partitionDP(data, begin, end, bounds, div);
        final int k0 = bounds[0];
        final int k1 = bounds[1];
        final int k2 = bounds[2];
        final int k3 = bounds[3];
        sortDP(data, begin, k0, div);
        sortDP(data, k3 + 1, end, div);
        sortDP(data, k1, k2 + 1, div);
    }









    void sortDP5(double[] data) {
        sortDP5(data, 0, sortNaN(data));
    }








    private void sortDP5(double[] data, int begin, int end) {
        if (end - begin <= minSelectSize) {
            insertionSort(data, begin, end, begin != 0);
            if (begin < end) {
                fixSignedZeros(data, begin, end);
            }
            return;
        }
        final int[] bounds = new int[4];
        partitionDP5(data, begin, end, bounds);
        final int k0 = bounds[0];
        final int k1 = bounds[1];
        final int k2 = bounds[2];
        final int k3 = bounds[3];
        sortDP5(data, begin, k0);
        sortDP5(data, k3 + 1, end);
        sortDP5(data, k1, k2 + 1);
    }








    static int[] createPivotsHeap(int length) {
        if (length <= MIN_SELECT_SIZE) {
            return NO_PIVOTS;
        }




        final int n = nextPow2(length >>> 1);
        final int[] pivotsHeap = new int[Math.min(n, 1 << 10) - 1];
        Arrays.fill(pivotsHeap, -1);
        return pivotsHeap;
    }










    private static int nextPow2(int value) {

        return 1 << -Integer.numberOfLeadingZeros(value - 1);
    }
}
