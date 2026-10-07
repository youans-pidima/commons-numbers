
















package org.apache.commons.numbers.arrays;













final class QuickSelect {









































































































    static final int MODE_FR_SAMPLING = -1;

    static final int MODE_SAMPLING = 0;

    static final int MODE_ADAPTION = 1;

    static final int MODE_STRICT = 2;




    private static final int MIN_SORTSELECT_SIZE = 4;







    private static final int LINEAR_SORTSELECT_SIZE = 24;











    private static final int DP_SORTSELECT_SIZE = 20;





    private static final int FR_SAMPLING_SIZE = 1200;




    private static final int RECURSION_INCREMENT = 1 << 20;



    private static final int SORTSELECT_MASK = RECURSION_INCREMENT - 1;


    private static final double STEP_LEFT = 0.4375;

    private static final double STEP_RIGHT = 0.5625;

    private static final double STEP_FAR_LEFT = 0.08333333333333333;

    private static final double STEP_FAR_RIGHT = 0.9166666666666666;


    private QuickSelect() {}











    static void heapSelect(double[] a, int left, int right, int ka, int kb) {
        if (right <= left) {
            return;
        }

        if (kb - left < right - ka) {
            heapSelectLeft(a, left, right, ka, kb);
        } else {
            heapSelectRight(a, left, right, ka, kb);
        }
    }














    static void heapSelectLeft(double[] a, int left, int right, int ka, int kb) {





        int end = kb + 1;
        for (int p = left + ((kb - left - 1) >> 1); p >= left; p--) {
            maxHeapSiftDown(a, a[p], p, left, end);
        }


        double max = a[left];
        for (int i = right; i > kb; i--) {
            final double v = a[i];
            if (v < max) {
                a[i] = max;
                maxHeapSiftDown(a, v, left, left, end);
                max = a[left];
            }
        }




        final int last = Math.max(left, ka - 1);
        while (--end > last) {
            maxHeapSiftDown(a, a[end], left, left, end);
            a[end] = max;
            max = a[left];
        }
    }












    private static void maxHeapSiftDown(double[] a, double v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root + 2;
            if (c > end) {

                break;
            }

            if (c == end || a[c] < a[c - 1]) {
                --c;
            }
            if (v >= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }














    static void heapSelectRight(double[] a, int left, int right, int ka, int kb) {





        int end = ka - 1;
        for (int p = right - ((right - ka - 1) >> 1); p <= right; p++) {
            minHeapSiftDown(a, a[p], p, right, end);
        }


        double min = a[right];
        for (int i = left; i < ka; i++) {
            final double v = a[i];
            if (v > min) {
                a[i] = min;
                minHeapSiftDown(a, v, right, right, end);
                min = a[right];
            }
        }




        final int last = Math.min(right, kb + 1);
        while (++end < last) {
            minHeapSiftDown(a, a[end], right, right, end);
            a[end] = min;
            min = a[right];
        }
    }












    private static void minHeapSiftDown(double[] a, double v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root - 2;
            if (c < end) {

                break;
            }

            if (c == end || a[c] > a[c + 1]) {
                ++c;
            }
            if (v <= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }











    static void sortSelect(double[] a, int left, int right, int ka, int kb) {


        if (right - left <= MIN_SORTSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            sortSelectLeft(a, left, right, kb);
        } else {
            sortSelectRight(a, left, right, ka);
        }
    }

















    static void sortSelectLeft(double[] a, int left, int right, int k) {

        for (int i = left; ++i <= k;) {
            final double v = a[i];

            if (v < a[i - 1]) {
                int j = i;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }


        double m = a[k];
        for (int i = right; i > k; i--) {
            final double v = a[i];
            if (v < m) {
                a[i] = m;
                int j = k;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
                m = a[k];
            }
        }
    }

















    static void sortSelectRight(double[] a, int left, int right, int k) {

        for (int i = right; --i >= k;) {
            final double v = a[i];

            if (v > a[i + 1]) {
                int j = i;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
            }
        }


        double m = a[k];
        for (int i = left; i < k; i++) {
            final double v = a[i];
            if (v > m) {
                a[i] = m;
                int j = k;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
                m = a[k];
            }
        }
    }












    static void select(double[] a, int left, int right, int k) {
        quickSelectAdaptive(a, left, right, k, k, new int[1], MODE_FR_SAMPLING);
    }















    static int select(double[] a, int left, int right, int[] k, int n) {
        if (n < 1) {
            return 0;
        }
        if (n == 1) {
            quickSelectAdaptive(a, left, right, k[0], k[0], new int[1], MODE_FR_SAMPLING);
            return -1;
        }


        final UpdatingInterval keys = IndexSupport.createUpdatingInterval(k, n);


        final int count = IndexSupport.countIndices(keys, n);




        final int k1 = keys.left();
        final int kn = keys.right();
        if (kn - k1 < DP_SORTSELECT_SIZE) {
            quickSelectAdaptive(a, left, right, k1, kn, new int[1], MODE_FR_SAMPLING);
        } else {

            dualPivotQuickSelect(a, left, right, keys, dualPivotFlags(left, right, k1, kn));
        }
        return count;
    }





























    static int quickSelectAdaptive(double[] a, int left, int right, int ka, int kb,
            int[] bounds, int flags) {
        int l = left;
        int r = right;
        int m = flags;
        while (true) {


            if (Math.min(kb - l, r - ka) < LINEAR_SORTSELECT_SIZE) {
                sortSelect(a, l, r, ka, kb);
                bounds[0] = kb;
                return ka;
            }


            int p0;
            final int n = r - l;

            final double f = (double) (ka - l) / n;





            int margin = n >> 2;
            if (m < MODE_SAMPLING && r - l > FR_SAMPLING_SIZE) {

                p0 = sampleStep(a, l, r, ka, bounds);
                if (f <= STEP_FAR_LEFT || f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                } else if (f > STEP_LEFT && f < STEP_RIGHT) {
                    margin -= n >> 5;
                }
            } else if (f <= STEP_LEFT) {
                if (f <= STEP_FAR_LEFT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarLeft(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepLeft(a, l, r, ka, bounds, m);
                }
            } else if (f >= STEP_RIGHT) {
                if (f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarRight(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepRight(a, l, r, ka, bounds, m);
                }
            } else {
                margin -= n >> 5;
                p0 = repeatedStep(a, l, r, ka, bounds, m);
            }






            final int p1 = bounds[0];
            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[0] = kb;
                }
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    p0 = ka;
                }
                return p0;
            }

            if (r - l > n - margin) {
                m++;
            }
        }
    }


















    private static int sampleStep(double[] a, int l, int r, int k, int[] upper) {



        final int n = r - l + 1;
        final int ith = k - l + 1;
        final double z = Math.log(n);

        final double s = 0.5 * Math.exp(0.6666666666666666 * z);
        final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
        final int ll = Math.max(l, (int) (k - ith * s / n + sd));
        final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

        final int p = quickSelectAdaptive(a, ll, rr, k, k, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, ll, rr, p, upper[0], upper);
    }























    private static int repeatedStep(double[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;

            s = k - mapDistance(k - l, l, r, fp);
            p = k;
        } else {

            fp = (r - l + 1) / 9;
            final int f3 = 3 * fp;
            final int end = l + (f3 << 1);
            for (int i = l + f3; i < end; i++) {
                Sorting.sort3(a, i - f3, i, i + f3);
            }

            s = l + (fp << 2);

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepLeft(double[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            s = Math.max(k - mapDistance(k - l, l, r, fp), l + fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                Sorting.lowerMedian4(a, i - f, i, i + f, i + f2);
            }

            fp = f / 3;
            s = l + f + fp;

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepRight(double[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            e = Math.min(k + mapDistance(r - k, l, r, fp), r - fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                Sorting.upperMedian4(a, i - f2, i - f, i, i + f);
            }

            fp = f / 3;
            e = r - f - fp;

            p = e - (flags == MODE_ADAPTION ? mapDistance(r - k, l, r, fp) : (fp >>> 1));
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarLeft(double[] a, int l, int r, int k, int[] upper, int flags) {









        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            s = l + fp;

            p = s + mapDistance(k - l, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                if (a[i + f] < a[i - f]) {
                    final double u = a[i + f];
                    a[i + f] = a[i - f];
                    a[i - f] = u;
                }
                if (a[i + f2] < a[i]) {
                    final double v = a[i + f2];
                    a[i + f2] = a[i];
                    a[i] = v;
                }
                if (a[i] < a[i - f]) {
                    final double u = a[i];
                    a[i] = a[i - f];
                    a[i - f] = u;
                }
            }

            fp = f / 3;
            s = l + fp;



            p = s + ((k - l) >>> 1);
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarRight(double[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            e = r - fp;

            p = e - mapDistance(r - k, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                if (a[i - f] > a[i + f]) {
                    final double u = a[i - f];
                    a[i - f] = a[i + f];
                    a[i + f] = u;
                }
                if (a[i - f2] > a[i]) {
                    final double v = a[i - f2];
                    a[i - f2] = a[i];
                    a[i] = v;
                }
                if (a[i] > a[i + f]) {
                    final double u = a[i];
                    a[i] = a[i + f];
                    a[i + f] = u;
                }
            }

            fp = f / 3;
            e = r - fp;



            p = e - ((r - k) >>> 1);
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }


























    static int expandPartition(double[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {


















        final double v = a[pivot0];

        double vi = a[start];
        double vj = a[end];
        a[start] = a[left];
        a[end] = a[right];
        a[left] = vj;
        a[right] = vi;

        int i = start + 1;
        int j = end - 1;


        int p0 = pivot0 == start ? i : pivot0;
        int p1 = pivot1 == end ? j : pivot1;

        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            vj = a[i];
            vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (vi == v) {
                a[i] = a[--p0];
                a[p0] = v;
            }
            if (vj == v) {
                a[j] = a[++p1];
                a[p1] = v;
            }




            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);
                    final double w = a[j];

                    a[j] = a[++p1];
                    a[p1] = v;

                    if (w != v) {
                        a[p0] = w;
                        p0++;
                    }
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);
                    final double w = a[i];

                    a[i] = a[--p0];
                    a[p0] = v;

                    if (w != v) {
                        a[p1] = w;
                        p1--;
                    }
                }
                break;
            }
        }

        upper[0] = p1;
        return p0;
    }






























    static void dualPivotQuickSelect(double[] a, int left, int right, UpdatingInterval k, int flags) {



        int l = left;
        int r = right;
        int f = flags;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0, 0, 0};
        while (true) {



            final int n = r - l;
            if (Math.min(kb - l, r - ka) < DP_SORTSELECT_SIZE ||
                n < (f & SORTSELECT_MASK)) {
                sortSelect(a, l, r, ka, kb);
                return;
            }
            if (kb - ka < DP_SORTSELECT_SIZE) {

                quickSelectAdaptive(a, l, r, ka, kb, upper, MODE_FR_SAMPLING);
                return;
            }
            if (f < 0) {

                heapSelect(a, l, r, ka, kb);
                return;
            }


            final int p0 = partition(a, l, r, upper);
            final int p1 = upper[0];








            f += RECURSION_INCREMENT;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p0 - 1, k.splitLeft(p0, p1), f);

                ka = k.left();
            } else if (kb <= p1) {

                return;
            } else if (ka <= p1) {

                ka = k.updateLeft(p1 + 1);
            }

            final int p2 = upper[1];
            final int p3 = upper[2];
            if (ka < p2) {
                l = p1 + 1;
                if (kb <= p3) {

                    r = p2 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p2 - 1, k.splitLeft(p2, p3), f);
                ka = k.left();
            } else if (kb <= p3) {

                return;
            } else if (ka <= p3) {
                ka = k.updateLeft(p3 + 1);
            }

            l = p3 + 1;
        }
    }

































    private static int partition(double[] a, int left, int right, int[] bounds) {




        final int n = right - left;
        final int step = 1 + (n >>> 3) + (n >>> 6);
        final int i3 = left + (n >>> 1);
        final int i2 = i3 - step;
        final int i1 = i2 - step;
        final int i4 = i3 + step;
        final int i5 = i4 + step;
        Sorting.sort5(a, i1, i2, i3, i4, i5);


















        final double v1 = a[i2];
        a[i2] = a[left];
        a[left] = v1;
        final double v2 = a[i4];
        a[i4] = a[right];
        a[right] = v2;


        int less = left;
        int great = right;



        do {
            ++less;
        } while (a[less] < v1);
        do {
            --great;
        } while (a[great] > v2);



        SORTING:
        for (int k = less; k <= great; k++) {
            final double v = a[k];
            if (v < v1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > v2) {


                while (a[great] > v2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final double w = a[great];
                a[great] = v;
                great--;

                if (w < v1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }


        less--;
        great++;

        a[left] = a[less];
        a[less] = v1;
        a[right] = a[great];
        a[great] = v2;


        final int lower = less;
        bounds[2] = great;










        if ((great - less) > (n >>> 1) + (n >>> 3) && v1 != v2) {



            do {
                ++less;
            } while (a[less] == v1);
            do {
                --great;
            } while (a[great] == v2);


            EQUAL:
            for (int k = less; k <= great; k++) {
                final double v = a[k];
                if (v == v1) {
                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == v2) {
                    while (a[great] == v2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final double w = a[great];
                    a[great] = v;
                    great--;
                    if (w == v1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }


            less--;
            great++;
        }


        if (v1 != v2 && less < great - 1) {

            bounds[0] = less;
            bounds[1] = great;
        } else {

            bounds[0] = bounds[2];
            bounds[1] = lower;
        }

        return lower;
    }











    static void heapSelect(int[] a, int left, int right, int ka, int kb) {
        if (right <= left) {
            return;
        }

        if (kb - left < right - ka) {
            heapSelectLeft(a, left, right, ka, kb);
        } else {
            heapSelectRight(a, left, right, ka, kb);
        }
    }














    static void heapSelectLeft(int[] a, int left, int right, int ka, int kb) {





        int end = kb + 1;
        for (int p = left + ((kb - left - 1) >> 1); p >= left; p--) {
            maxHeapSiftDown(a, a[p], p, left, end);
        }


        int max = a[left];
        for (int i = right; i > kb; i--) {
            final int v = a[i];
            if (v < max) {
                a[i] = max;
                maxHeapSiftDown(a, v, left, left, end);
                max = a[left];
            }
        }




        final int last = Math.max(left, ka - 1);
        while (--end > last) {
            maxHeapSiftDown(a, a[end], left, left, end);
            a[end] = max;
            max = a[left];
        }
    }












    private static void maxHeapSiftDown(int[] a, int v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root + 2;
            if (c > end) {

                break;
            }

            if (c == end || a[c] < a[c - 1]) {
                --c;
            }
            if (v >= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }














    static void heapSelectRight(int[] a, int left, int right, int ka, int kb) {





        int end = ka - 1;
        for (int p = right - ((right - ka - 1) >> 1); p <= right; p++) {
            minHeapSiftDown(a, a[p], p, right, end);
        }


        int min = a[right];
        for (int i = left; i < ka; i++) {
            final int v = a[i];
            if (v > min) {
                a[i] = min;
                minHeapSiftDown(a, v, right, right, end);
                min = a[right];
            }
        }




        final int last = Math.min(right, kb + 1);
        while (++end < last) {
            minHeapSiftDown(a, a[end], right, right, end);
            a[end] = min;
            min = a[right];
        }
    }












    private static void minHeapSiftDown(int[] a, int v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root - 2;
            if (c < end) {

                break;
            }

            if (c == end || a[c] > a[c + 1]) {
                ++c;
            }
            if (v <= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }











    static void sortSelect(int[] a, int left, int right, int ka, int kb) {


        if (right - left <= MIN_SORTSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            sortSelectLeft(a, left, right, kb);
        } else {
            sortSelectRight(a, left, right, ka);
        }
    }

















    static void sortSelectLeft(int[] a, int left, int right, int k) {

        for (int i = left; ++i <= k;) {
            final int v = a[i];

            if (v < a[i - 1]) {
                int j = i;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }


        int m = a[k];
        for (int i = right; i > k; i--) {
            final int v = a[i];
            if (v < m) {
                a[i] = m;
                int j = k;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
                m = a[k];
            }
        }
    }

















    static void sortSelectRight(int[] a, int left, int right, int k) {

        for (int i = right; --i >= k;) {
            final int v = a[i];

            if (v > a[i + 1]) {
                int j = i;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
            }
        }


        int m = a[k];
        for (int i = left; i < k; i++) {
            final int v = a[i];
            if (v > m) {
                a[i] = m;
                int j = k;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
                m = a[k];
            }
        }
    }












    static void select(int[] a, int left, int right, int k) {
        quickSelectAdaptive(a, left, right, k, k, new int[1], MODE_FR_SAMPLING);
    }











    static void select(int[] a, int left, int right, int[] k, int n) {
        if (n == 1) {
            quickSelectAdaptive(a, left, right, k[0], k[0], new int[1], MODE_FR_SAMPLING);
            return;
        }


        final UpdatingInterval keys = IndexSupport.createUpdatingInterval(k, n);




        final int k1 = keys.left();
        final int kn = keys.right();
        if (kn - k1 < DP_SORTSELECT_SIZE) {
            quickSelectAdaptive(a, left, right, k1, kn, new int[1], MODE_FR_SAMPLING);
        } else {

            dualPivotQuickSelect(a, left, right, keys, dualPivotFlags(left, right, k1, kn));
        }
    }





























    static int quickSelectAdaptive(int[] a, int left, int right, int ka, int kb,
            int[] bounds, int flags) {
        int l = left;
        int r = right;
        int m = flags;
        while (true) {


            if (Math.min(kb - l, r - ka) < LINEAR_SORTSELECT_SIZE) {
                sortSelect(a, l, r, ka, kb);
                bounds[0] = kb;
                return ka;
            }


            int p0;
            final int n = r - l;

            final double f = (double) (ka - l) / n;





            int margin = n >> 2;
            if (m < MODE_SAMPLING && r - l > FR_SAMPLING_SIZE) {

                p0 = sampleStep(a, l, r, ka, bounds);
                if (f <= STEP_FAR_LEFT || f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                } else if (f > STEP_LEFT && f < STEP_RIGHT) {
                    margin -= n >> 5;
                }
            } else if (f <= STEP_LEFT) {
                if (f <= STEP_FAR_LEFT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarLeft(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepLeft(a, l, r, ka, bounds, m);
                }
            } else if (f >= STEP_RIGHT) {
                if (f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarRight(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepRight(a, l, r, ka, bounds, m);
                }
            } else {
                margin -= n >> 5;
                p0 = repeatedStep(a, l, r, ka, bounds, m);
            }






            final int p1 = bounds[0];
            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[0] = kb;
                }
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    p0 = ka;
                }
                return p0;
            }

            if (r - l > n - margin) {
                m++;
            }
        }
    }


















    private static int sampleStep(int[] a, int l, int r, int k, int[] upper) {



        final int n = r - l + 1;
        final int ith = k - l + 1;
        final double z = Math.log(n);

        final double s = 0.5 * Math.exp(0.6666666666666666 * z);
        final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
        final int ll = Math.max(l, (int) (k - ith * s / n + sd));
        final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

        final int p = quickSelectAdaptive(a, ll, rr, k, k, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, ll, rr, p, upper[0], upper);
    }























    private static int repeatedStep(int[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;

            s = k - mapDistance(k - l, l, r, fp);
            p = k;
        } else {

            fp = (r - l + 1) / 9;
            final int f3 = 3 * fp;
            final int end = l + (f3 << 1);
            for (int i = l + f3; i < end; i++) {
                Sorting.sort3(a, i - f3, i, i + f3);
            }

            s = l + (fp << 2);

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepLeft(int[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            s = Math.max(k - mapDistance(k - l, l, r, fp), l + fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                Sorting.lowerMedian4(a, i - f, i, i + f, i + f2);
            }

            fp = f / 3;
            s = l + f + fp;

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepRight(int[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            e = Math.min(k + mapDistance(r - k, l, r, fp), r - fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                Sorting.upperMedian4(a, i - f2, i - f, i, i + f);
            }

            fp = f / 3;
            e = r - f - fp;

            p = e - (flags == MODE_ADAPTION ? mapDistance(r - k, l, r, fp) : (fp >>> 1));
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarLeft(int[] a, int l, int r, int k, int[] upper, int flags) {









        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            s = l + fp;

            p = s + mapDistance(k - l, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                if (a[i + f] < a[i - f]) {
                    final int u = a[i + f];
                    a[i + f] = a[i - f];
                    a[i - f] = u;
                }
                if (a[i + f2] < a[i]) {
                    final int v = a[i + f2];
                    a[i + f2] = a[i];
                    a[i] = v;
                }
                if (a[i] < a[i - f]) {
                    final int u = a[i];
                    a[i] = a[i - f];
                    a[i - f] = u;
                }
            }

            fp = f / 3;
            s = l + fp;



            p = s + ((k - l) >>> 1);
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarRight(int[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            e = r - fp;

            p = e - mapDistance(r - k, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                if (a[i - f] > a[i + f]) {
                    final int u = a[i - f];
                    a[i - f] = a[i + f];
                    a[i + f] = u;
                }
                if (a[i - f2] > a[i]) {
                    final int v = a[i - f2];
                    a[i - f2] = a[i];
                    a[i] = v;
                }
                if (a[i] > a[i + f]) {
                    final int u = a[i];
                    a[i] = a[i + f];
                    a[i + f] = u;
                }
            }

            fp = f / 3;
            e = r - fp;



            p = e - ((r - k) >>> 1);
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }


























    static int expandPartition(int[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {


















        final int v = a[pivot0];

        int vi = a[start];
        int vj = a[end];
        a[start] = a[left];
        a[end] = a[right];
        a[left] = vj;
        a[right] = vi;

        int i = start + 1;
        int j = end - 1;


        int p0 = pivot0 == start ? i : pivot0;
        int p1 = pivot1 == end ? j : pivot1;

        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            vj = a[i];
            vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (vi == v) {
                a[i] = a[--p0];
                a[p0] = v;
            }
            if (vj == v) {
                a[j] = a[++p1];
                a[p1] = v;
            }




            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);
                    final int w = a[j];

                    a[j] = a[++p1];
                    a[p1] = v;

                    if (w != v) {
                        a[p0] = w;
                        p0++;
                    }
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);
                    final int w = a[i];

                    a[i] = a[--p0];
                    a[p0] = v;

                    if (w != v) {
                        a[p1] = w;
                        p1--;
                    }
                }
                break;
            }
        }

        upper[0] = p1;
        return p0;
    }






























    static void dualPivotQuickSelect(int[] a, int left, int right, UpdatingInterval k, int flags) {



        int l = left;
        int r = right;
        int f = flags;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0, 0, 0};
        while (true) {



            final int n = r - l;
            if (Math.min(kb - l, r - ka) < DP_SORTSELECT_SIZE ||
                n < (f & SORTSELECT_MASK)) {
                sortSelect(a, l, r, ka, kb);
                return;
            }
            if (kb - ka < DP_SORTSELECT_SIZE) {

                quickSelectAdaptive(a, l, r, ka, kb, upper, MODE_FR_SAMPLING);
                return;
            }
            if (f < 0) {

                heapSelect(a, l, r, ka, kb);
                return;
            }


            final int p0 = partition(a, l, r, upper);
            final int p1 = upper[0];








            f += RECURSION_INCREMENT;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p0 - 1, k.splitLeft(p0, p1), f);

                ka = k.left();
            } else if (kb <= p1) {

                return;
            } else if (ka <= p1) {

                ka = k.updateLeft(p1 + 1);
            }

            final int p2 = upper[1];
            final int p3 = upper[2];
            if (ka < p2) {
                l = p1 + 1;
                if (kb <= p3) {

                    r = p2 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p2 - 1, k.splitLeft(p2, p3), f);
                ka = k.left();
            } else if (kb <= p3) {

                return;
            } else if (ka <= p3) {
                ka = k.updateLeft(p3 + 1);
            }

            l = p3 + 1;
        }
    }

































    private static int partition(int[] a, int left, int right, int[] bounds) {




        final int n = right - left;
        final int step = 1 + (n >>> 3) + (n >>> 6);
        final int i3 = left + (n >>> 1);
        final int i2 = i3 - step;
        final int i1 = i2 - step;
        final int i4 = i3 + step;
        final int i5 = i4 + step;
        Sorting.sort5(a, i1, i2, i3, i4, i5);


















        final int v1 = a[i2];
        a[i2] = a[left];
        a[left] = v1;
        final int v2 = a[i4];
        a[i4] = a[right];
        a[right] = v2;


        int less = left;
        int great = right;



        do {
            ++less;
        } while (a[less] < v1);
        do {
            --great;
        } while (a[great] > v2);



        SORTING:
        for (int k = less; k <= great; k++) {
            final int v = a[k];
            if (v < v1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > v2) {


                while (a[great] > v2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final int w = a[great];
                a[great] = v;
                great--;

                if (w < v1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }


        less--;
        great++;

        a[left] = a[less];
        a[less] = v1;
        a[right] = a[great];
        a[great] = v2;


        final int lower = less;
        bounds[2] = great;










        if ((great - less) > (n >>> 1) + (n >>> 3) && v1 != v2) {



            do {
                ++less;
            } while (a[less] == v1);
            do {
                --great;
            } while (a[great] == v2);


            EQUAL:
            for (int k = less; k <= great; k++) {
                final int v = a[k];
                if (v == v1) {
                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == v2) {
                    while (a[great] == v2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final int w = a[great];
                    a[great] = v;
                    great--;
                    if (w == v1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }


            less--;
            great++;
        }


        if (v1 != v2 && less < great - 1) {

            bounds[0] = less;
            bounds[1] = great;
        } else {

            bounds[0] = bounds[2];
            bounds[1] = lower;
        }

        return lower;
    }











    static void heapSelect(long[] a, int left, int right, int ka, int kb) {
        if (right <= left) {
            return;
        }

        if (kb - left < right - ka) {
            heapSelectLeft(a, left, right, ka, kb);
        } else {
            heapSelectRight(a, left, right, ka, kb);
        }
    }














    static void heapSelectLeft(long[] a, int left, int right, int ka, int kb) {





        int end = kb + 1;
        for (int p = left + ((kb - left - 1) >> 1); p >= left; p--) {
            maxHeapSiftDown(a, a[p], p, left, end);
        }


        long max = a[left];
        for (int i = right; i > kb; i--) {
            final long v = a[i];
            if (v < max) {
                a[i] = max;
                maxHeapSiftDown(a, v, left, left, end);
                max = a[left];
            }
        }




        final int last = Math.max(left, ka - 1);
        while (--end > last) {
            maxHeapSiftDown(a, a[end], left, left, end);
            a[end] = max;
            max = a[left];
        }
    }












    private static void maxHeapSiftDown(long[] a, long v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root + 2;
            if (c > end) {

                break;
            }

            if (c == end || a[c] < a[c - 1]) {
                --c;
            }
            if (v >= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }














    static void heapSelectRight(long[] a, int left, int right, int ka, int kb) {





        int end = ka - 1;
        for (int p = right - ((right - ka - 1) >> 1); p <= right; p++) {
            minHeapSiftDown(a, a[p], p, right, end);
        }


        long min = a[right];
        for (int i = left; i < ka; i++) {
            final long v = a[i];
            if (v > min) {
                a[i] = min;
                minHeapSiftDown(a, v, right, right, end);
                min = a[right];
            }
        }




        final int last = Math.min(right, kb + 1);
        while (++end < last) {
            minHeapSiftDown(a, a[end], right, right, end);
            a[end] = min;
            min = a[right];
        }
    }












    private static void minHeapSiftDown(long[] a, long v, int p, int root, int end) {


        while (true) {

            int c = (p << 1) - root - 2;
            if (c < end) {

                break;
            }

            if (c == end || a[c] > a[c + 1]) {
                ++c;
            }
            if (v <= a[c]) {

                break;
            }

            a[p] = a[c];
            p = c;
        }
        a[p] = v;
    }











    static void sortSelect(long[] a, int left, int right, int ka, int kb) {


        if (right - left <= MIN_SORTSELECT_SIZE) {
            Sorting.sort(a, left, right);
            return;
        }

        if (kb - left < right - ka) {
            sortSelectLeft(a, left, right, kb);
        } else {
            sortSelectRight(a, left, right, ka);
        }
    }

















    static void sortSelectLeft(long[] a, int left, int right, int k) {

        for (int i = left; ++i <= k;) {
            final long v = a[i];

            if (v < a[i - 1]) {
                int j = i;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
            }
        }


        long m = a[k];
        for (int i = right; i > k; i--) {
            final long v = a[i];
            if (v < m) {
                a[i] = m;
                int j = k;
                while (--j >= left && v < a[j]) {
                    a[j + 1] = a[j];
                }
                a[j + 1] = v;
                m = a[k];
            }
        }
    }

















    static void sortSelectRight(long[] a, int left, int right, int k) {

        for (int i = right; --i >= k;) {
            final long v = a[i];

            if (v > a[i + 1]) {
                int j = i;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
            }
        }


        long m = a[k];
        for (int i = left; i < k; i++) {
            final long v = a[i];
            if (v > m) {
                a[i] = m;
                int j = k;
                while (++j <= right && v > a[j]) {
                    a[j - 1] = a[j];
                }
                a[j - 1] = v;
                m = a[k];
            }
        }
    }












    static void select(long[] a, int left, int right, int k) {
        quickSelectAdaptive(a, left, right, k, k, new int[1], MODE_FR_SAMPLING);
    }











    static void select(long[] a, int left, int right, int[] k, int n) {
        if (n == 1) {
            quickSelectAdaptive(a, left, right, k[0], k[0], new int[1], MODE_FR_SAMPLING);
            return;
        }


        final UpdatingInterval keys = IndexSupport.createUpdatingInterval(k, n);




        final int k1 = keys.left();
        final int kn = keys.right();
        if (kn - k1 < DP_SORTSELECT_SIZE) {
            quickSelectAdaptive(a, left, right, k1, kn, new int[1], MODE_FR_SAMPLING);
        } else {

            dualPivotQuickSelect(a, left, right, keys, dualPivotFlags(left, right, k1, kn));
        }
    }





























    static int quickSelectAdaptive(long[] a, int left, int right, int ka, int kb,
            int[] bounds, int flags) {
        int l = left;
        int r = right;
        int m = flags;
        while (true) {


            if (Math.min(kb - l, r - ka) < LINEAR_SORTSELECT_SIZE) {
                sortSelect(a, l, r, ka, kb);
                bounds[0] = kb;
                return ka;
            }


            int p0;
            final int n = r - l;

            final double f = (double) (ka - l) / n;





            int margin = n >> 2;
            if (m < MODE_SAMPLING && r - l > FR_SAMPLING_SIZE) {

                p0 = sampleStep(a, l, r, ka, bounds);
                if (f <= STEP_FAR_LEFT || f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                } else if (f > STEP_LEFT && f < STEP_RIGHT) {
                    margin -= n >> 5;
                }
            } else if (f <= STEP_LEFT) {
                if (f <= STEP_FAR_LEFT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarLeft(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepLeft(a, l, r, ka, bounds, m);
                }
            } else if (f >= STEP_RIGHT) {
                if (f >= STEP_FAR_RIGHT) {
                    margin += (n >> 5) + (n >> 6);
                    p0 = repeatedStepFarRight(a, l, r, ka, bounds, m);
                } else {
                    p0 = repeatedStepRight(a, l, r, ka, bounds, m);
                }
            } else {
                margin -= n >> 5;
                p0 = repeatedStep(a, l, r, ka, bounds, m);
            }






            final int p1 = bounds[0];
            if (kb < p0) {

                r = p0 - 1;
            } else if (ka > p1) {

                l = p1 + 1;
            } else {




                if (kb > p1) {
                    sortSelectLeft(a, p1 + 1, r, kb);
                    bounds[0] = kb;
                }
                if (ka < p0) {
                    sortSelectRight(a, l, p0 - 1, ka);
                    p0 = ka;
                }
                return p0;
            }

            if (r - l > n - margin) {
                m++;
            }
        }
    }


















    private static int sampleStep(long[] a, int l, int r, int k, int[] upper) {



        final int n = r - l + 1;
        final int ith = k - l + 1;
        final double z = Math.log(n);

        final double s = 0.5 * Math.exp(0.6666666666666666 * z);
        final double sd = 0.5 * Math.sqrt(z * s * (n - s) / n) * Integer.signum(ith - (n >> 1));
        final int ll = Math.max(l, (int) (k - ith * s / n + sd));
        final int rr = Math.min(r, (int) (k + (n - ith) * s / n + sd));

        final int p = quickSelectAdaptive(a, ll, rr, k, k, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, ll, rr, p, upper[0], upper);
    }























    private static int repeatedStep(long[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;

            s = k - mapDistance(k - l, l, r, fp);
            p = k;
        } else {

            fp = (r - l + 1) / 9;
            final int f3 = 3 * fp;
            final int end = l + (f3 << 1);
            for (int i = l + f3; i < end; i++) {
                Sorting.sort3(a, i - f3, i, i + f3);
            }

            s = l + (fp << 2);

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepLeft(long[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            s = Math.max(k - mapDistance(k - l, l, r, fp), l + fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                Sorting.lowerMedian4(a, i - f, i, i + f, i + f2);
            }

            fp = f / 3;
            s = l + f + fp;

            p = s + (flags == MODE_ADAPTION ? mapDistance(k - l, l, r, fp) : (fp >>> 1));
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }
























    private static int repeatedStepRight(long[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;


            e = Math.min(k + mapDistance(r - k, l, r, fp), r - fp);
            p = k;
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                Sorting.upperMedian4(a, i - f2, i - f, i, i + f);
            }

            fp = f / 3;
            e = r - f - fp;

            p = e - (flags == MODE_ADAPTION ? mapDistance(r - k, l, r, fp) : (fp >>> 1));
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarLeft(long[] a, int l, int r, int k, int[] upper, int flags) {









        final int fp;
        final int s;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            s = l + fp;

            p = s + mapDistance(k - l, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = l + f2;
            for (int i = l + f; i < end; i++) {
                if (a[i + f] < a[i - f]) {
                    final long u = a[i + f];
                    a[i + f] = a[i - f];
                    a[i - f] = u;
                }
                if (a[i + f2] < a[i]) {
                    final long v = a[i + f2];
                    a[i + f2] = a[i];
                    a[i] = v;
                }
                if (a[i] < a[i - f]) {
                    final long u = a[i];
                    a[i] = a[i - f];
                    a[i - f] = u;
                }
            }

            fp = f / 3;
            s = l + fp;



            p = s + ((k - l) >>> 1);
        }
        final int e = s + fp - 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }























    private static int repeatedStepFarRight(long[] a, int l, int r, int k, int[] upper, int flags) {

        final int fp;
        final int e;
        int p;
        if (flags <= MODE_SAMPLING) {

            fp = (r - l + 1) / 12;
            e = r - fp;

            p = e - mapDistance(r - k, l, r, fp);
        } else {

            final int f = (r - l + 1) >> 2;
            final int f2 = f + f;
            final int end = r - f2;
            for (int i = r - f; i > end; i--) {
                if (a[i - f] > a[i + f]) {
                    final long u = a[i - f];
                    a[i - f] = a[i + f];
                    a[i + f] = u;
                }
                if (a[i - f2] > a[i]) {
                    final long v = a[i - f2];
                    a[i - f2] = a[i];
                    a[i] = v;
                }
                if (a[i] > a[i + f]) {
                    final long u = a[i];
                    a[i] = a[i + f];
                    a[i + f] = u;
                }
            }

            fp = f / 3;
            e = r - fp;



            p = e - ((r - k) >>> 1);
        }
        final int s = e - fp + 1;
        for (int i = s; i <= e; i++) {
            Sorting.sort3(a, i - fp, i, i + fp);
        }
        p = quickSelectAdaptive(a, s, e, p, p, upper, MODE_FR_SAMPLING);
        return expandPartition(a, l, r, s, e, p, upper[0], upper);
    }


























    static int expandPartition(long[] a, int left, int right, int start, int end,
        int pivot0, int pivot1, int[] upper) {


















        final long v = a[pivot0];

        long vi = a[start];
        long vj = a[end];
        a[start] = a[left];
        a[end] = a[right];
        a[left] = vj;
        a[right] = vi;

        int i = start + 1;
        int j = end - 1;


        int p0 = pivot0 == start ? i : pivot0;
        int p1 = pivot1 == end ? j : pivot1;

        while (true) {
            do {
                --i;
            } while (a[i] < v);
            do {
                ++j;
            } while (a[j] > v);
            vj = a[i];
            vi = a[j];
            a[i] = vi;
            a[j] = vj;

            if (vi == v) {
                a[i] = a[--p0];
                a[p0] = v;
            }
            if (vj == v) {
                a[j] = a[++p1];
                a[p1] = v;
            }




            if (i == left) {
                while (j < right) {
                    do {
                        ++j;
                    } while (a[j] > v);
                    final long w = a[j];

                    a[j] = a[++p1];
                    a[p1] = v;

                    if (w != v) {
                        a[p0] = w;
                        p0++;
                    }
                }
                break;
            }
            if (j == right) {
                while (i > left) {
                    do {
                        --i;
                    } while (a[i] < v);
                    final long w = a[i];

                    a[i] = a[--p0];
                    a[p0] = v;

                    if (w != v) {
                        a[p1] = w;
                        p1--;
                    }
                }
                break;
            }
        }

        upper[0] = p1;
        return p0;
    }






























    static void dualPivotQuickSelect(long[] a, int left, int right, UpdatingInterval k, int flags) {



        int l = left;
        int r = right;
        int f = flags;
        int ka = k.left();
        int kb = k.right();
        final int[] upper = {0, 0, 0};
        while (true) {



            final int n = r - l;
            if (Math.min(kb - l, r - ka) < DP_SORTSELECT_SIZE ||
                n < (f & SORTSELECT_MASK)) {
                sortSelect(a, l, r, ka, kb);
                return;
            }
            if (kb - ka < DP_SORTSELECT_SIZE) {

                quickSelectAdaptive(a, l, r, ka, kb, upper, MODE_FR_SAMPLING);
                return;
            }
            if (f < 0) {

                heapSelect(a, l, r, ka, kb);
                return;
            }


            final int p0 = partition(a, l, r, upper);
            final int p1 = upper[0];








            f += RECURSION_INCREMENT;

            if (ka < p0) {
                if (kb <= p1) {

                    r = p0 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p0 - 1, k.splitLeft(p0, p1), f);

                ka = k.left();
            } else if (kb <= p1) {

                return;
            } else if (ka <= p1) {

                ka = k.updateLeft(p1 + 1);
            }

            final int p2 = upper[1];
            final int p3 = upper[2];
            if (ka < p2) {
                l = p1 + 1;
                if (kb <= p3) {

                    r = p2 - 1;
                    if (r < kb) {
                        kb = k.updateRight(r);
                    }
                    continue;
                }
                dualPivotQuickSelect(a, l, p2 - 1, k.splitLeft(p2, p3), f);
                ka = k.left();
            } else if (kb <= p3) {

                return;
            } else if (ka <= p3) {
                ka = k.updateLeft(p3 + 1);
            }

            l = p3 + 1;
        }
    }

































    private static int partition(long[] a, int left, int right, int[] bounds) {




        final int n = right - left;
        final int step = 1 + (n >>> 3) + (n >>> 6);
        final int i3 = left + (n >>> 1);
        final int i2 = i3 - step;
        final int i1 = i2 - step;
        final int i4 = i3 + step;
        final int i5 = i4 + step;
        Sorting.sort5(a, i1, i2, i3, i4, i5);


















        final long v1 = a[i2];
        a[i2] = a[left];
        a[left] = v1;
        final long v2 = a[i4];
        a[i4] = a[right];
        a[right] = v2;


        int less = left;
        int great = right;



        do {
            ++less;
        } while (a[less] < v1);
        do {
            --great;
        } while (a[great] > v2);



        SORTING:
        for (int k = less; k <= great; k++) {
            final long v = a[k];
            if (v < v1) {

                a[k] = a[less];
                a[less] = v;
                less++;
            } else if (v > v2) {


                while (a[great] > v2) {
                    if (great-- == k) {

                        break SORTING;
                    }
                }



                final long w = a[great];
                a[great] = v;
                great--;

                if (w < v1) {
                    a[k] = a[less];
                    a[less] = w;
                    less++;
                } else {
                    a[k] = w;
                }
            }
        }


        less--;
        great++;

        a[left] = a[less];
        a[less] = v1;
        a[right] = a[great];
        a[great] = v2;


        final int lower = less;
        bounds[2] = great;










        if ((great - less) > (n >>> 1) + (n >>> 3) && v1 != v2) {



            do {
                ++less;
            } while (a[less] == v1);
            do {
                --great;
            } while (a[great] == v2);


            EQUAL:
            for (int k = less; k <= great; k++) {
                final long v = a[k];
                if (v == v1) {
                    a[k] = a[less];
                    a[less] = v;
                    less++;
                } else if (v == v2) {
                    while (a[great] == v2) {
                        if (great-- == k) {

                            break EQUAL;
                        }
                    }
                    final long w = a[great];
                    a[great] = v;
                    great--;
                    if (w == v1) {
                        a[k] = a[less];
                        a[less] = w;
                        less++;
                    } else {
                        a[k] = w;
                    }
                }
            }


            less--;
            great++;
        }


        if (v1 != v2 && less < great - 1) {

            bounds[0] = less;
            bounds[1] = great;
        } else {

            bounds[0] = bounds[2];
            bounds[1] = lower;
        }

        return lower;
    }















    private static int mapDistance(int d, int l, int r, int n) {
        return (int) (d * (n - 1.0) / (r - l));
    }











    private static int dualPivotFlags(int left, int right, int k1, int kn) {
        final int maxDepth = dualPivotMaxDepth(right - left);
        final int ss = dualPivotSortSelectSize(k1, kn);
        return dualPivotFlags(maxDepth, ss);
    }









    static int dualPivotFlags(int maxDepth, int ss) {


        int flags = Integer.MIN_VALUE - maxDepth * RECURSION_INCREMENT;
        flags &= ~SORTSELECT_MASK;
        return flags | ss;
    }











    static int dualPivotMaxDepth(int x) {



        return ((32 - Integer.numberOfLeadingZeros(x)) * 323) >>> 8;
    }








    private static int dualPivotSortSelectSize(int k1, int kn) {























































































        if (kn - k1 < DP_SORTSELECT_SIZE * 3) {
            return 0;
        }


        return DP_SORTSELECT_SIZE * 2;
    }
}
