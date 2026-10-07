















package org.apache.commons.numbers.examples.jmh.arrays;

import java.util.Arrays;













final class Sorting {

    private static final int UNIQUE_INSERTION_SORT = 20;


    private Sorting() {}














    static void sort(double[] data, int left, int right, boolean internal) {
        if (internal) {












            for (int i = left; ++i <= right;) {
                final double v = data[i];

                if (v < data[i - 1]) {
                    int j = i;
                    while (v < data[--j]) {
                        data[j + 1] = data[j];
                    }
                    data[j + 1] = v;
                }
            }
        } else {
            for (int i = left; ++i <= right;) {
                final double v = data[i];

                if (v < data[i - 1]) {
                    int j = i;
                    while (--j >= left && v < data[j]) {
                        data[j + 1] = data[j];
                    }
                    data[j + 1] = v;
                }
            }
        }
    }










    static void sort(double[] data, int left, int right) {
        for (int i = left; ++i <= right;) {
            final double v = data[i];

            if (v < data[i - 1]) {
                int j = i;
                while (--j >= left && v < data[j]) {
                    data[j + 1] = data[j];
                }
                data[j + 1] = v;
            }
        }
    }










    static void sortb(double[] data, int left, int right) {
        for (int i = left; ++i <= right;) {
            final double v = data[i];



            int j = i;
            while (--j >= left && v < data[j]) {
                data[j + 1] = data[j];
            }
            data[j + 1] = v;
        }
    }










    static void sortPairedInternal1(double[] data, int left, int right) {










        for (int i = left + ((right - left + 1) & 0x1); i < right; i += 2) {
            double v1 = data[i];
            double v2 = data[i + 1];

            if (v2 < v1) {
                v1 = v2;
                v2 = data[i];
            }

            int j = i;
            while (v2 < data[--j]) {
                data[j + 2] = data[j];
            }

            data[++j + 1] = v2;

            while (v1 < data[--j]) {
                data[j + 1] = data[j];
            }
            data[j + 1] = v1;
        }
    }










    static void sortPairedInternal2(double[] data, int left, int right) {










        for (int i = left + ((right - left + 1) & 0x1), j = i; ++j <= right; i = ++j) {
            double v1 = data[i];
            double v2 = data[j];

            if (v2 < v1) {
                v1 = v2;
                v2 = data[i];
            }

            while (v2 < data[--i]) {
                data[i + 2] = data[i];
            }

            data[++i + 1] = v2;

            while (v1 < data[--i]) {
                data[i + 1] = data[i];
            }
            data[i + 1] = v1;
        }
    }










    static void sortPairedInternal3(double[] data, int left, int right) {










        for (int i = left + ((right - left + 1) & 0x1), j = i; ++j <= right; i = ++j) {
            double v1 = data[i];
            double v2 = data[j];

            if (v2 < v1) {
                v1 = v2;
                v2 = data[i];

                data[j] = v2;
            }

            if (v2 < data[i - 1]) {
                while (v2 < data[--i]) {
                    data[i + 2] = data[i];
                }

                data[++i + 1] = v2;
            }

            if (v1 < data[i - 1]) {
                while (v1 < data[--i]) {
                    data[i + 1] = data[i];
                }

                i++;
            }

            data[i] = v1;
        }
    }










    static void sortPairedInternal4(double[] data, int left, int right) {










        for (int i = left + ((right - left + 1) & 0x1), j = i; ++j <= right; i = ++j) {
            double v1 = data[i];
            double v2 = data[j];

            if (v2 < v1) {
                v1 = v2;
                v2 = data[i];

                data[j] = v2;
                data[i] = v1;
            }

            if (v1 < data[i - 1]) {

                while (v2 < data[--i]) {
                    data[i + 2] = data[i];
                }

                data[++i + 1] = v2;

                while (v1 < data[--i]) {
                    data[i + 1] = data[i];
                }
                data[i + 1] = v1;
            }
        }
    }










    static void min3(double[] x, int a, int b, int c) {
        if (x[b] < x[a]) {
            final double v = x[b];
            x[b] = x[a];
            x[a] = v;
        }
        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }
    }










    static void max3(double[] x, int a, int b, int c) {
        if (x[c] < x[b]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
        }
        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }
    }




















    static void sort3(double[] data, int i0, int i1, int i2) {



        final double x = data[i0];
        final double y = data[i1];
        final double z = data[i2];
        if (z < x) {
            if (y < z) {
                data[i0] = y;
                data[i1] = z;
                data[i2] = x;
                return;
            }
            if (x < y) {
                data[i0] = z;
                data[i1] = x;
                data[i2] = y;
                return;
            }

            data[i0] = z;
            data[i2] = x;
            return;
        }
        if (y < x) {

            data[i0] = y;
            data[i1] = x;
            return;
        }
        if (z < y) {

            data[i1] = z;
            data[i2] = y;
        }

    }























    static void sort3b(double[] data, int i0, int i1, int i2) {



        if (data[i2] < data[i0]) {
            final double v = data[i2];
            data[i2] = data[i0];
            data[i0] = v;
        }
        if (data[i2] < data[i1]) {
            final double v = data[i2];
            data[i2] = data[i1];
            data[i1] = v;
        } else if (data[i1] < data[i0]) {
            final double v = data[i1];
            data[i1] = data[i0];
            data[i0] = v;
        }
    }

























    static void sort3c(double[] data, int i0, int i1, int i2) {




        if (data[i2] < data[i0]) {
            final double v = data[i2];
            data[i2] = data[i0];
            data[i0] = v;
        }
        if (data[i1] < data[i0]) {
            final double v = data[i1];
            data[i1] = data[i0];
            data[i0] = v;
        }
        if (data[i2] < data[i1]) {
            final double v = data[i2];
            data[i2] = data[i1];
            data[i1] = v;
        }
    }





















    static void sort4(double[] data, int i0, int i1, int i2, int i3) {






        if (data[i3] < data[i1]) {
            final double u = data[i3];
            data[i3] = data[i1];
            data[i1] = u;
        }
        if (data[i2] < data[i0]) {
            final double v = data[i2];
            data[i2] = data[i0];
            data[i0] = v;
        }

        if (data[i3] < data[i2]) {
            final double u = data[i3];
            data[i3] = data[i2];
            data[i2] = u;
        }
        if (data[i1] < data[i0]) {
            final double v = data[i1];
            data[i1] = data[i0];
            data[i0] = v;
        }

        if (data[i2] < data[i1]) {
            final double u = data[i2];
            data[i2] = data[i1];
            data[i1] = u;
        }
    }






















    static void sort5(double[] data, int i0, int i1, int i2, int i3, int i4) {








        if (data[i4] < data[i1]) {
            final double u = data[i4];
            data[i4] = data[i1];
            data[i1] = u;
        }
        if (data[i3] < data[i0]) {
            final double v = data[i3];
            data[i3] = data[i0];
            data[i0] = v;
        }

        if (data[i3] < data[i1]) {
            final double u = data[i3];
            data[i3] = data[i1];
            data[i1] = u;
        }
        if (data[i2] < data[i0]) {
            final double v = data[i2];
            data[i2] = data[i0];
            data[i0] = v;
        }

        if (data[i4] < data[i2]) {
            final double u = data[i4];
            data[i4] = data[i2];
            data[i2] = u;
        }
        if (data[i1] < data[i0]) {
            final double v = data[i1];
            data[i1] = data[i0];
            data[i0] = v;
        }

        if (data[i4] < data[i3]) {
            final double u = data[i4];
            data[i4] = data[i3];
            data[i3] = u;
        }
        if (data[i2] < data[i1]) {
            final double v = data[i2];
            data[i2] = data[i1];
            data[i1] = v;
        }

        if (data[i3] < data[i2]) {
            final double u = data[i3];
            data[i3] = data[i2];
            data[i2] = u;
        }
    }






















    static void sort5b(double[] data, int i0, int i1, int i2, int i3, int i4) {









        sort4(data, i0, i1, i3, i4);
        final double u = data[i2];
        if (u > data[i3]) {
            data[i2] = data[i3];
            data[i3] = u;
            if (u > data[i4]) {
                data[i3] = data[i4];
                data[i4] = u;
            }
        } else if (u < data[i1]) {
            data[i2] = data[i1];
            data[i1] = u;
            if (u < data[i0]) {
                data[i1] = data[i0];
                data[i0] = u;
            }
        }
    }






















    static void sort5c(double[] data, int i0, int i1, int i2, int i3, int i4) {






        if (data[i1] < data[i0]) {
            final double u = data[i1];
            data[i1] = data[i0];
            data[i0] = u;
        }
        if (data[i3] < data[i2]) {
            final double v = data[i3];
            data[i3] = data[i2];
            data[i2] = v;
        }



        if (data[i3] < data[i1]) {
            final double u = data[i0];
            final double v = data[i1];
            data[i0] = data[i2];
            data[i1] = data[i3];
            data[i2] = u;
            data[i3] = v;
        }


        final double e = data[i4];
        if (e < data[i1]) {
            if (e < data[i0]) {

                data[i4] = data[i3];
                data[i3] = data[i1];
                data[i1] = data[i0];
                data[i0] = e;
            } else {

                data[i4] = data[i3];
                data[i3] = data[i1];
                data[i1] = e;
            }
        } else {
            if (data[i4] < data[i3]) {

                data[i4] = data[i3];
                data[i3] = e;
            }

        }


        final double c = data[i2];
        if (c < data[i1]) {
            if (c < data[i0]) {
                data[i2] = data[i1];
                data[i1] = data[i0];
                data[i0] = c;
            } else {
                data[i2] = data[i1];
                data[i1] = c;
            }
        } else {
            if (data[i3] < c) {
                data[i2] = data[i3];
                data[i3] = c;
            }

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











    static void lowerMedian4b(double[] x, int a, int b, int c, int d) {


        if (x[d] < x[b]) {
            final double u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[c] < x[b]) {
            final double v = x[c];
            x[c] = x[b];
            x[b] = v;
        }

        if (x[b] < x[a]) {
            final double w = x[b];
            x[b] = x[a];
            x[a] = w;


            if (x[d] < x[b]) {
                final double u = x[d];
                x[d] = x[b];
                x[b] = u;
            }
            if (x[c] < x[b]) {
                final double v = x[c];
                x[c] = x[b];
                x[b] = v;
            }
        }
    }











    static void lowerMedian4c(double[] x, int a, int b, int c, int d) {

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


        if (x[b] < x[a]) {
            final double v = x[a];
            final double u = x[c];
            x[a] = x[b];
            x[c] = x[d];
            x[b] = v;
            x[d] = u;
        }


        if (x[c] < x[b]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
        }
    }











    static void lowerMedian4d(double[] x, int a, int b, int c, int d) {

        if (x[d] < x[a]) {
            final double u = x[d];
            x[d] = x[a];
            x[a] = u;
        }
        if (x[c] < x[b]) {
            final double v = x[c];
            x[c] = x[b];
            x[b] = v;
        }


        if (x[b] < x[a]) {
            final double xb = x[a];
            x[a] = x[b];
            x[b] = xb;


            if (x[c] < xb) {
                x[b] = x[c];
                x[c] = xb;

            }


        } else if (x[d] < x[b]) {


            final double v = x[d];

            x[d] = x[c];
            x[c] = x[b];
            x[b] = v;



        }
    }











    static void lowerMedian4e(double[] x, int a, int b, int c, int d) {

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


        if (x[b] < x[a]) {
            final double xb = x[a];
            x[a] = x[b];
            x[b] = xb;


            if (x[d] < xb) {
                x[b] = x[d];


                x[d] = x[c];
                x[c] = xb;
            }


        } else if (x[c] < x[b]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
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











    static void upperMedian4c(double[] x, int a, int b, int c, int d) {

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


        if (x[d] < x[c]) {
            final double v = x[a];
            final double u = x[c];
            x[a] = x[b];
            x[c] = x[d];
            x[b] = v;
            x[d] = u;
        }


        if (x[c] < x[b]) {
            final double u = x[c];
            x[c] = x[b];
            x[b] = u;
        }
    }











    static void upperMedian4d(double[] x, int a, int b, int c, int d) {

        if (x[d] < x[a]) {
            final double u = x[d];
            x[d] = x[a];
            x[a] = u;
        }
        if (x[c] < x[b]) {
            final double v = x[c];
            x[c] = x[b];
            x[b] = v;
        }


        if (x[d] < x[c]) {
            final double xc = x[d];
            x[d] = x[c];
            x[c] = xc;


            if (xc < x[b]) {
                x[c] = x[b];
                x[b] = xc;

            }


        } else if (x[c] < x[a]) {


            final double v = x[a];

            x[a] = x[b];
            x[b] = x[c];
            x[c] = v;



        }
    }









    static int median5(double[] a, int i1) {
        final int i2 = i1 + 1;
        final int i3 = i1 + 2;
        final int i4 = i1 + 3;
        final int i5 = i1 + 4;



        if (a[i2] < a[i1]) {
            final double v = a[i2];
            a[i2] = a[i1];
            a[i1] = v;
        }
        if (a[i4] < a[i3]) {
            final double v = a[i4];
            a[i4] = a[i3];
            a[i3] = v;
        }

        if (a[i1] < a[i3]) {

            if (a[i5] < a[i2]) {
                final double v = a[i5];
                a[i5] = a[i2];
                a[i2] = v;
            }

            if (a[i2] < a[i3]) {

                return a[i5] < a[i3] ? i5 : i3;
            }

            return a[i2] < a[i4] ? i2 : i4;
        }

        if (a[i5] < a[i4]) {
            final double v = a[i5];
            a[i5] = a[i4];
            a[i4] = v;
        }

        if (a[i1] < a[i4]) {

            return a[i2] < a[i4] ? i2 : i4;
        }

        return a[i1] < a[i5] ? i1 : i5;
    }












    static int median5(double[] a, int i1, int i2, int i3, int i4, int i5) {



        if (a[i2] < a[i1]) {
            final double v = a[i2];
            a[i2] = a[i1];
            a[i1] = v;
        }
        if (a[i4] < a[i3]) {
            final double v = a[i4];
            a[i4] = a[i3];
            a[i3] = v;
        }

        if (a[i1] < a[i3]) {

            if (a[i5] < a[i2]) {
                final double v = a[i5];
                a[i5] = a[i2];
                a[i2] = v;
            }

            if (a[i2] < a[i3]) {

                return a[i5] < a[i3] ? i5 : i3;
            }

            return a[i2] < a[i4] ? i2 : i4;
        }

        if (a[i5] < a[i4]) {
            final double v = a[i5];
            a[i5] = a[i4];
            a[i4] = v;
        }

        if (a[i1] < a[i4]) {

            return a[i2] < a[i4] ? i2 : i4;
        }

        return a[i1] < a[i5] ? i1 : i5;
    }









    static int median5b(double[] a, int i1) {
        final int i2 = i1 + 1;
        final int i3 = i1 + 2;
        final int i4 = i1 + 3;
        final int i5 = i1 + 4;



        if (a[i2] < a[i1]) {
            final double v = a[i2];
            a[i2] = a[i1];
            a[i1] = v;
        }
        if (a[i4] < a[i3]) {
            final double v = a[i4];
            a[i4] = a[i3];
            a[i3] = v;
        }

        if (a[i1] < a[i3]) {

            if (a[i5] < a[i2]) {

                if (a[i5] < a[i3]) {

                    return a[i2] < a[i3] ? i2 : i3;
                }

                return a[i5] < a[i4] ? i5 : i4;
            }

            if (a[i2] < a[i3]) {

                return a[i5] < a[i3] ? i5 : i3;
            }

            return a[i2] < a[i4] ? i2 : i4;
        }

        if (a[i5] < a[i4]) {

            if (a[i1] < a[i5]) {

                return a[i2] < a[i5] ? i2 : i5;
            }

            return a[i1] < a[i4] ? i1 : i4;
        }

        if (a[i1] < a[i4]) {

            return a[i2] < a[i4] ? i2 : i4;
        }

        return a[i1] < a[i5] ? i1 : i5;
    }









    static int median5c(double[] a, int i1) {

        Sorting.sort4(a, i1, i1 + 1, i1 + 3, i1 + 4);

        int m = i1 + 2;
        if (a[m] < a[m - 1]) {
            --m;
        } else if (a[m] > a[m + 1]) {
            ++m;
        }
        return m;
    }












    static void median5d(double[] x, int a, int b, int c, int d, int e) {



        if (x[c] < x[a]) {
            final double v = x[c];
            x[c] = x[a];
            x[a] = v;
        }
        if (x[d] < x[b]) {
            final double u = x[d];
            x[d] = x[b];
            x[b] = u;
        }
        if (x[d] < x[c]) {
            final double v = x[d];
            x[d] = x[c];
            x[c] = v;
            final double u = x[b];
            x[b] = x[a];
            x[a] = u;
        }
        if (x[e] < x[b]) {
            final double v = x[e];
            x[e] = x[b];
            x[b] = v;
        }
        if (x[e] < x[c]) {
            final double u = x[e];
            x[e] = x[c];
            x[c] = u;
            if (u < x[a]) {
                x[c] = x[a];
                x[a] = u;
            }
        } else {
            if (x[c] < x[b]) {
                final double u = x[c];
                x[c] = x[b];
                x[b] = u;
            }
        }
    }
























    static void sort7(double[] data, int i0, int i1, int i2, int i3, int i4, int i5, int i6) {









        if (data[i5] < data[i4]) {
            final double u = data[i5];
            data[i5] = data[i4];
            data[i4] = u;
        }
        if (data[i3] < data[i2]) {
            final double v = data[i3];
            data[i3] = data[i2];
            data[i2] = v;
        }
        if (data[i6] < data[i0]) {
            final double w = data[i6];
            data[i6] = data[i0];
            data[i0] = w;
        }

        if (data[i6] < data[i3]) {
            final double u = data[i6];
            data[i6] = data[i3];
            data[i3] = u;
        }
        if (data[i4] < data[i1]) {
            final double v = data[i4];
            data[i4] = data[i1];
            data[i1] = v;
        }
        if (data[i2] < data[i0]) {
            final double w = data[i2];
            data[i2] = data[i0];
            data[i0] = w;
        }

        if (data[i4] < data[i3]) {
            final double u = data[i4];
            data[i4] = data[i3];
            data[i3] = u;
        }
        if (data[i5] < data[i2]) {
            final double v = data[i5];
            data[i5] = data[i2];
            data[i2] = v;
        }
        if (data[i1] < data[i0]) {
            final double w = data[i1];
            data[i1] = data[i0];
            data[i0] = w;
        }

        if (data[i6] < data[i4]) {
            final double u = data[i6];
            data[i6] = data[i4];
            data[i4] = u;
        }
        if (data[i2] < data[i1]) {
            final double v = data[i2];
            data[i2] = data[i1];
            data[i1] = v;
        }

        if (data[i5] < data[i4]) {
            final double u = data[i5];
            data[i5] = data[i4];
            data[i4] = u;
        }
        if (data[i3] < data[i2]) {
            final double v = data[i3];
            data[i3] = data[i2];
            data[i2] = v;
        }

        if (data[i6] < data[i5]) {
            final double u = data[i6];
            data[i6] = data[i5];
            data[i5] = u;
        }
        if (data[i4] < data[i3]) {
            final double v = data[i4];
            data[i4] = data[i3];
            data[i3] = v;
        }
        if (data[i2] < data[i1]) {
            final double w = data[i2];
            data[i2] = data[i1];
            data[i1] = w;
        }
    }

























    static void sort8(double[] data, int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {









        if (data[i7] < data[i5]) {
            final double u = data[i7];
            data[i7] = data[i5];
            data[i5] = u;
        }
        if (data[i6] < data[i4]) {
            final double v = data[i6];
            data[i6] = data[i4];
            data[i4] = v;
        }
        if (data[i3] < data[i1]) {
            final double w = data[i3];
            data[i3] = data[i1];
            data[i1] = w;
        }
        if (data[i2] < data[i0]) {
            final double x = data[i2];
            data[i2] = data[i0];
            data[i0] = x;
        }

        if (data[i7] < data[i3]) {
            final double u = data[i7];
            data[i7] = data[i3];
            data[i3] = u;
        }
        if (data[i6] < data[i2]) {
            final double v = data[i6];
            data[i6] = data[i2];
            data[i2] = v;
        }
        if (data[i5] < data[i1]) {
            final double w = data[i5];
            data[i5] = data[i1];
            data[i1] = w;
        }
        if (data[i4] < data[i0]) {
            final double x = data[i4];
            data[i4] = data[i0];
            data[i0] = x;
        }

        if (data[i7] < data[i6]) {
            final double u = data[i7];
            data[i7] = data[i6];
            data[i6] = u;
        }
        if (data[i5] < data[i4]) {
            final double v = data[i5];
            data[i5] = data[i4];
            data[i4] = v;
        }
        if (data[i3] < data[i2]) {
            final double w = data[i3];
            data[i3] = data[i2];
            data[i2] = w;
        }
        if (data[i1] < data[i0]) {
            final double x = data[i1];
            data[i1] = data[i0];
            data[i0] = x;
        }

        if (data[i5] < data[i3]) {
            final double u = data[i5];
            data[i5] = data[i3];
            data[i3] = u;
        }
        if (data[i4] < data[i2]) {
            final double v = data[i4];
            data[i4] = data[i2];
            data[i2] = v;
        }

        if (data[i6] < data[i3]) {
            final double u = data[i6];
            data[i6] = data[i3];
            data[i3] = u;
        }
        if (data[i4] < data[i1]) {
            final double v = data[i4];
            data[i4] = data[i1];
            data[i1] = v;
        }

        if (data[i6] < data[i5]) {
            final double u = data[i6];
            data[i6] = data[i5];
            data[i5] = u;
        }
        if (data[i4] < data[i3]) {
            final double v = data[i4];
            data[i4] = data[i3];
            data[i3] = v;
        }
        if (data[i2] < data[i1]) {
            final double w = data[i2];
            data[i2] = data[i1];
            data[i1] = w;
        }
    }




























    static void sort11(double[] data, int i0, int i1, int i2, int i3, int i4, int i5, int i6, int i7,
        int i8, int i9, int i10) {











        if (data[i8] < data[i5]) {
            final double u = data[i8];
            data[i8] = data[i5];
            data[i5] = u;
        }
        if (data[i7] < data[i3]) {
            final double v = data[i7];
            data[i7] = data[i3];
            data[i3] = v;
        }
        if (data[i4] < data[i2]) {
            final double w = data[i4];
            data[i4] = data[i2];
            data[i2] = w;
        }
        if (data[i6] < data[i1]) {
            final double x = data[i6];
            data[i6] = data[i1];
            data[i1] = x;
        }
        if (data[i9] < data[i0]) {
            final double y = data[i9];
            data[i9] = data[i0];
            data[i0] = y;
        }

        if (data[i8] < data[i7]) {
            final double u = data[i8];
            data[i8] = data[i7];
            data[i7] = u;
        }
        if (data[i9] < data[i6]) {
            final double v = data[i9];
            data[i9] = data[i6];
            data[i6] = v;
        }
        if (data[i10] < data[i4]) {
            final double w = data[i10];
            data[i10] = data[i4];
            data[i4] = w;
        }
        if (data[i5] < data[i3]) {
            final double x = data[i5];
            data[i5] = data[i3];
            data[i3] = x;
        }
        if (data[i1] < data[i0]) {
            final double y = data[i1];
            data[i1] = data[i0];
            data[i0] = y;
        }

        if (data[i10] < data[i8]) {
            final double u = data[i10];
            data[i10] = data[i8];
            data[i8] = u;
        }
        if (data[i7] < data[i4]) {
            final double v = data[i7];
            data[i7] = data[i4];
            data[i4] = v;
        }
        if (data[i5] < data[i2]) {
            final double w = data[i5];
            data[i5] = data[i2];
            data[i2] = w;
        }
        if (data[i3] < data[i1]) {
            final double x = data[i3];
            data[i3] = data[i1];
            data[i1] = x;
        }

        if (data[i8] < data[i6]) {
            final double u = data[i8];
            data[i8] = data[i6];
            data[i6] = u;
        }
        if (data[i9] < data[i5]) {
            final double v = data[i9];
            data[i9] = data[i5];
            data[i5] = v;
        }
        if (data[i7] < data[i3]) {
            final double w = data[i7];
            data[i7] = data[i3];
            data[i3] = w;
        }
        if (data[i2] < data[i1]) {
            final double x = data[i2];
            data[i2] = data[i1];
            data[i1] = x;
        }
        if (data[i4] < data[i0]) {
            final double y = data[i4];
            data[i4] = data[i0];
            data[i0] = y;
        }

        if (data[i10] < data[i9]) {
            final double u = data[i10];
            data[i10] = data[i9];
            data[i9] = u;
        }
        if (data[i8] < data[i7]) {
            final double v = data[i8];
            data[i8] = data[i7];
            data[i7] = v;
        }
        if (data[i5] < data[i4]) {
            final double w = data[i5];
            data[i5] = data[i4];
            data[i4] = w;
        }
        if (data[i6] < data[i2]) {
            final double x = data[i6];
            data[i6] = data[i2];
            data[i2] = x;
        }
        if (data[i1] < data[i0]) {
            final double y = data[i1];
            data[i1] = data[i0];
            data[i0] = y;
        }

        if (data[i9] < data[i8]) {
            final double u = data[i9];
            data[i9] = data[i8];
            data[i8] = u;
        }
        if (data[i7] < data[i5]) {
            final double v = data[i7];
            data[i7] = data[i5];
            data[i5] = v;
        }
        if (data[i6] < data[i3]) {
            final double w = data[i6];
            data[i6] = data[i3];
            data[i3] = w;
        }
        if (data[i4] < data[i2]) {
            final double x = data[i4];
            data[i4] = data[i2];
            data[i2] = x;
        }

        if (data[i8] < data[i7]) {
            final double u = data[i8];
            data[i8] = data[i7];
            data[i7] = u;
        }
        if (data[i6] < data[i5]) {
            final double v = data[i6];
            data[i6] = data[i5];
            data[i5] = v;
        }
        if (data[i4] < data[i3]) {
            final double w = data[i4];
            data[i4] = data[i3];
            data[i3] = w;
        }
        if (data[i2] < data[i1]) {
            final double x = data[i2];
            data[i2] = data[i1];
            data[i1] = x;
        }

        if (data[i7] < data[i6]) {
            final double u = data[i7];
            data[i7] = data[i6];
            data[i6] = u;
        }
        if (data[i5] < data[i4]) {
            final double v = data[i5];
            data[i5] = data[i4];
            data[i4] = v;
        }
        if (data[i3] < data[i2]) {
            final double w = data[i3];
            data[i3] = data[i2];
            data[i2] = w;
        }
    }





























    static IndexSet sortUnique(int countThreshold, int[] data, int n) {
        if (n <= 1) {
            return null;
        }
        if (n > countThreshold) {
            return sortUnique(data, n);
        }
        int unique = 1;
        int j;

        for (int i = 0; ++i < n;) {
            final int v = data[i];

            data[i] = -1;
            j = unique;
            if (v > data[j - 1]) {

                data[j] = v;
                unique++;
            } else if (v < data[j - 1]) {

                do {
                    --j;
                } while (j >= 0 && v < data[j]);

                if (j < 0 || v != data[j]) {

                    j++;



                    for (int k = unique; k-- > j;) {
                        data[k + 1] = data[k];
                    }
                    data[j] = v;
                    unique++;
                }
            }
        }

        if (unique < n) {
            data[n - 1] = ~data[unique - 1];
        }
        return null;
    }
























    private static IndexSet sortUnique(int[] data, int n) {
        final IndexSet set = IndexSet.of(data, n);

        final int[] unique = {0};
        set.forEach(i -> data[unique[0]++] = i);
        if (unique[0] < n) {
            for (int i = unique[0]; i < n; i++) {
                data[i] = -1;
            }

            data[n - 1] = ~data[unique[0] - 1];
        }
        return set;
    }


















    static int sortIndices(int[] data, int n) {

        if (n < 3) {
            if (n == 2) {
                final int i0 = data[0];
                final int i1 = data[1];
                if (i0 > i1) {
                    data[0] = i1;
                    data[1] = i0;
                } else if (i0 == i1) {
                    return 1;
                }
            }

            return n;
        }












        if (n <= UNIQUE_INSERTION_SORT) {
            return sortIndicesInsertionSort(data, n);
        }

        if (isAscending(data, n)) {
            return compressDuplicates(data, n);
        }



        int min = data[0];
        int max = min;
        for (int i = 0; ++i < n;) {
            min = Math.min(min, data[i]);
            max = Math.max(max, data[i]);
        }




























        if (HashIndexSet.memoryFootprint(n) < (IndexSet.memoryFootprint(min, max) >>> 3)) {
            return sortIndicesHashIndexSet(data, n);
        }


        final IndexSet set = IndexSet.ofRange(min, max);
        for (int i = -1; ++i < n;) {
            set.set(data[i]);
        }
        return set.toArray(data);
    }




















    static int sortIndices2(int[] data, int n) {

        if (n < 3) {
            if (n == 2) {
                final int i0 = data[0];
                final int i1 = data[1];
                if (i0 > i1) {
                    data[0] = i1;
                    data[1] = i0;
                } else if (i0 == i1) {
                    return 1;
                }
            }

            return n;
        }












        if (n <= UNIQUE_INSERTION_SORT) {
            return sortIndicesInsertionSort(data, n);
        }

        if (isAscending(data, n)) {
            return compressDuplicates(data, n);
        }

        return sortIndicesHashIndexSet(data, n);
    }









    private static boolean isAscending(int[] data, int n) {
        for (int i = 0; ++i < n;) {
            if (data[i] < data[i - 1]) {

                return false;
            }
        }
        return true;
    }










    static boolean isAscending(double[] data, int left, int right) {
        for (int i = left; ++i <= right;) {
            if (data[i] < data[i - 1]) {

                return false;
            }
        }
        return true;
    }
















    static int sortIndicesInsertionSort(int[] data, int n) {
        int unique = 1;

        for (int i = 0; ++i < n;) {
            final int v = data[i];
            int j = unique - 1;
            if (v > data[j]) {

                data[unique] = v;
                unique++;
            } else if (v < data[j]) {

                do {
                    --j;
                } while (j >= 0 && v < data[j]);


                if (j < 0 || v != data[j]) {

                    for (int k = unique; --k > j;) {
                        data[k + 1] = data[k];
                    }
                    data[j + 1] = v;
                    unique++;
                }
            }
        }
        return unique;
    }













    static int sortIndicesBinarySearch(int[] data, int n) {

        if (data[1] < data[0]) {
            final int v = data[0];
            data[0] = data[1];
            data[1] = v;
        }
        int unique = data[0] != data[1] ? 2 : 1;

        OUTER:
        for (int i = 1; ++i < n;) {

            int l = 0;
            int r = unique - 1;
            final int k = data[i];
            while (l <= r) {

                final int m = (l + r) >>> 1;
                final int v = data[m];




                if (v < k) {
                    l = m + 1;
                } else if (v > k) {
                    r = m - 1;
                } else {

                    continue OUTER;
                }
            }

            System.arraycopy(data, l, data, l + 1, unique - l);
            data[l] = k;
            unique++;
        }
        return unique;
    }













    static int sortIndicesHeapSort(int[] data, int n) {


        final int offset = n - 1;
        for (int start = offset >> 1; start >= 0; start--) {
            minHeapSiftDown(data, offset, start, n);
        }











        int previous = data[offset];
        data[offset] = data[0];
        data[0] = previous;
        int s = n - 1;
        minHeapSiftDown(data, offset, 0, s);




        int last = 0;
        for (;;) {
            s--;

            final int v = data[offset];
            data[offset] = data[offset - s];
            if (previous != v) {
                data[++last] = v;
                previous = v;
            }
            if (s == 1) {

                break;
            }
            minHeapSiftDown(data, offset, 0, s);
        }


        if (previous != data[offset]) {
            data[++last] = data[offset];
        }
        return last + 1;
    }












    private static void minHeapSiftDown(int[] a, int offset, int root, int n) {






        int p = root;
        final int v = a[offset - p];

        int c = (p << 1) + 1;
        while (c < n) {

            int cv = a[offset - c];

            if (c + 1 < n && cv > a[offset - c - 1]) {
                cv = a[offset - c - 1];
                c++;
            }

            if (v <= cv) {

                break;
            }

            a[offset - p] = cv;
            p = c;
            c = (p << 1) + 1;
        }
        a[offset - p] = v;
    }













    static int sortIndicesSort(int[] data, int n) {
        java.util.Arrays.sort(data, 0, n);
        return compressDuplicates(data, n);
    }










    private static int compressDuplicates(int[] data, int n) {

        int last = 0;
        int top = data[0];
        for (int i = 0; ++i < n;) {
            final int v = data[i];
            if (v == top) {
                continue;
            }
            top = v;
            data[++last] = v;
        }
        return last + 1;
    }















    static int sortIndicesIndexSet(int[] data, int n) {



        return IndexSet.of(data, n).toArray(data);
    }















    static int sortIndicesIndexSet2(int[] data, int n) {



        return IndexSet.of(data, n).toArray2(data);
    }














    static int sortIndicesHashIndexSet(int[] data, int n) {



        final HashIndexSet set = new HashIndexSet(n);
        int i = 0;
        int last = 0;
        set.add(data[0]);
        while (++i < n) {
            final int v = data[i];
            if (set.add(v)) {
                data[++last] = v;
            }
        }


        Arrays.sort(data, 0, ++last);
        return last;
    }
}
