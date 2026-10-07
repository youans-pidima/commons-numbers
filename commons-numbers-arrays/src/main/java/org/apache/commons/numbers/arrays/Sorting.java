















package org.apache.commons.numbers.arrays;

import java.util.Arrays;














final class Sorting {


    private Sorting() {}








    static void sort(double[] x, int left, int right) {
        for (int i = left; ++i <= right;) {
            final double v = x[i];

            if (v < x[i - 1]) {
                int j = i;
                while (--j >= left && v < x[j]) {
                    x[j + 1] = x[j];
                }
                x[j + 1] = v;
            }
        }
    }









    static void sort3(double[] x, int a, int b, int c) {



        final double u = x[a];
        final double v = x[b];
        final double w = x[c];
        if (w < u) {
            if (v < w) {
                x[a] = v;
                x[b] = w;
                x[c] = u;
                return;
            }
            if (u < v) {
                x[a] = w;
                x[b] = u;
                x[c] = v;
                return;
            }

            x[a] = w;
            x[c] = u;
            return;
        }
        if (v < u) {

            x[a] = v;
            x[b] = u;
            return;
        }
        if (w < v) {

            x[b] = w;
            x[c] = v;
        }

    }











    static void sort5(double[] x, int a, int b, int c, int d, int e) {








        if (x[e] < x[b]) {
            final double u = x[e];
            x[e] = x[b];
            x[b] = u;
        }
        if (x[d] < x[a]) {
            final double v = x[d];
            x[d] = x[a];
            x[a] = v;
        }

        if (x[d] < x[b]) {
            final double u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }

        if (x[e] < x[c]) {
            final double u = x[e];
            x[e] = x[c];
            x[c] = u;
        }
        if (x[b] < x[a]) {
            final double v = x[b];
            x[b] = x[a];
            x[a] = v;
        }

        if (x[e] < x[d]) {
            final double u = x[e];
            x[e] = x[d];
            x[d] = u;
        }
        if (x[c] < x[b]) {
            final double v = x[c];
            x[c] = x[b];
            x[b] = v;
        }

        if (x[d] < x[c]) {
            final double u = x[d];
            x[d] = x[c];
            x[c] = u;
        }
    }











    static void lowerMedian4(double[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final double u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[c] < x[b]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[b] < x[a]) {


            final double xb = x[a];
            x[a] = x[b];
            x[b] = xb;


            if (x[d] < xb) {
                x[b] = x[d];

                x[d] = x[c];
                x[c] = xb;
            }
        }
    }











    static void upperMedian4(double[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final double u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[b] > x[c]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[c] > x[d]) {


            final double xc = x[d];
            x[d] = x[c];
            x[c] = xc;


            if (x[a] > xc) {
                x[c] = x[a];

                x[a] = x[b];
                x[b] = xc;
            }
        }
    }








    static void sort(int[] x, int left, int right) {
        for (int i = left; ++i <= right;) {
            final int v = x[i];

            if (v < x[i - 1]) {
                int j = i;
                while (--j >= left && v < x[j]) {
                    x[j + 1] = x[j];
                }
                x[j + 1] = v;
            }
        }
    }









    static void sort3(int[] x, int a, int b, int c) {



        final int u = x[a];
        final int v = x[b];
        final int w = x[c];
        if (w < u) {
            if (v < w) {
                x[a] = v;
                x[b] = w;
                x[c] = u;
                return;
            }
            if (u < v) {
                x[a] = w;
                x[b] = u;
                x[c] = v;
                return;
            }

            x[a] = w;
            x[c] = u;
            return;
        }
        if (v < u) {

            x[a] = v;
            x[b] = u;
            return;
        }
        if (w < v) {

            x[b] = w;
            x[c] = v;
        }

    }











    static void sort5(int[] x, int a, int b, int c, int d, int e) {








        if (x[e] < x[b]) {
            final int u = x[e];
            x[e] = x[b];
            x[b] = u;
        }
        if (x[d] < x[a]) {
            final int v = x[d];
            x[d] = x[a];
            x[a] = v;
        }

        if (x[d] < x[b]) {
            final int u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final int v = x[c];
            x[c] = x[a];
            x[a] = v;
        }

        if (x[e] < x[c]) {
            final int u = x[e];
            x[e] = x[c];
            x[c] = u;
        }
        if (x[b] < x[a]) {
            final int v = x[b];
            x[b] = x[a];
            x[a] = v;
        }

        if (x[e] < x[d]) {
            final int u = x[e];
            x[e] = x[d];
            x[d] = u;
        }
        if (x[c] < x[b]) {
            final int v = x[c];
            x[c] = x[b];
            x[b] = v;
        }

        if (x[d] < x[c]) {
            final int u = x[d];
            x[d] = x[c];
            x[c] = u;
        }
    }











    static void lowerMedian4(int[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final int u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final int v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[c] < x[b]) {
            final int u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[b] < x[a]) {


            final int xb = x[a];
            x[a] = x[b];
            x[b] = xb;


            if (x[d] < xb) {
                x[b] = x[d];

                x[d] = x[c];
                x[c] = xb;
            }
        }
    }











    static void upperMedian4(int[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final int u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final int v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[b] > x[c]) {
            final int u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[c] > x[d]) {


            final int xc = x[d];
            x[d] = x[c];
            x[c] = xc;


            if (x[a] > xc) {
                x[c] = x[a];

                x[a] = x[b];
                x[b] = xc;
            }
        }
    }








    static void sort(long[] x, int left, int right) {
        for (int i = left; ++i <= right;) {
            final long v = x[i];

            if (v < x[i - 1]) {
                int j = i;
                while (--j >= left && v < x[j]) {
                    x[j + 1] = x[j];
                }
                x[j + 1] = v;
            }
        }
    }









    static void sort3(long[] x, int a, int b, int c) {



        final long u = x[a];
        final long v = x[b];
        final long w = x[c];
        if (w < u) {
            if (v < w) {
                x[a] = v;
                x[b] = w;
                x[c] = u;
                return;
            }
            if (u < v) {
                x[a] = w;
                x[b] = u;
                x[c] = v;
                return;
            }

            x[a] = w;
            x[c] = u;
            return;
        }
        if (v < u) {

            x[a] = v;
            x[b] = u;
            return;
        }
        if (w < v) {

            x[b] = w;
            x[c] = v;
        }

    }











    static void sort5(long[] x, int a, int b, int c, int d, int e) {








        if (x[e] < x[b]) {
            final long u = x[e];
            x[e] = x[b];
            x[b] = u;
        }
        if (x[d] < x[a]) {
            final long v = x[d];
            x[d] = x[a];
            x[a] = v;
        }

        if (x[d] < x[b]) {
            final long u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final long v = x[c];
            x[c] = x[a];
            x[a] = v;
        }

        if (x[e] < x[c]) {
            final long u = x[e];
            x[e] = x[c];
            x[c] = u;
        }
        if (x[b] < x[a]) {
            final long v = x[b];
            x[b] = x[a];
            x[a] = v;
        }

        if (x[e] < x[d]) {
            final long u = x[e];
            x[e] = x[d];
            x[d] = u;
        }
        if (x[c] < x[b]) {
            final long v = x[c];
            x[c] = x[b];
            x[b] = v;
        }

        if (x[d] < x[c]) {
            final long u = x[d];
            x[d] = x[c];
            x[c] = u;
        }
    }











    static void lowerMedian4(long[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final long u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final long v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[c] < x[b]) {
            final long u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[b] < x[a]) {


            final long xb = x[a];
            x[a] = x[b];
            x[b] = xb;


            if (x[d] < xb) {
                x[b] = x[d];

                x[d] = x[c];
                x[c] = xb;
            }
        }
    }











    static void upperMedian4(long[] x, int a, int b, int c, int d) {

        if (x[d] < x[b]) {
            final long u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final long v = x[c];
            x[c] = x[a];
            x[a] = v;
        }


        if (x[b] > x[c]) {
            final long u = x[c];
            x[c] = x[b];
            x[b] = u;
        } else if (x[c] > x[d]) {


            final long xc = x[d];
            x[d] = x[c];
            x[c] = xc;


            if (x[a] > xc) {
                x[c] = x[a];

                x[a] = x[b];
                x[b] = xc;
            }
        }
    }















    static int insertionSortIndices(int[] x, int n) {

        int unique = 0;

        for (int i = 1; i < n; i++) {
            final int v = x[i];
            int j = unique;
            if (v > x[j]) {

                x[++unique] = v;
            } else if (v < x[j]) {

                do {
                    --j;
                } while (j >= 0 && v < x[j]);


                if (j < 0 || v != x[j]) {

                    for (int k = unique; k > j; --k) {
                        x[k + 1] = x[k];
                    }
                    x[j + 1] = v;
                    ++unique;
                }
            }
        }
        return unique + 1;
    }















    static int sortIndices(int[] x, int n) {


        final HashIndexSet set = HashIndexSet.create(n);
        int i = 0;
        int last = 0;
        set.add(x[0]);
        while (++i < n) {
            final int v = x[i];
            if (set.add(v)) {
                x[++last] = v;
            }
        }
        Arrays.sort(x, 0, ++last);
        return last;
    }
}
