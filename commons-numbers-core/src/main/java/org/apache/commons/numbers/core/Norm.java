















package org.apache.commons.numbers.core;










public enum Norm {




    L1(Norm::manhattan, Norm::manhattan, Norm::manhattan),

    MANHATTAN(L1),

    L2(Norm::euclidean, Norm::euclidean, Norm::euclidean),

    EUCLIDEAN(L2),




    LINF(Norm::maximum, Norm::maximum, Norm::maximum),

    MAXIMUM(LINF);






    private static final double SMALL_THRESH = 0x1.0p-511;





    private static final double LARGE_THRESH = 0x1.0p+496;




    private static final double SAFE_SCALE_UP_THRESH = 0x1.0p-100;

    private static final double SCALE_DOWN = 0x1.0p-600;

    private static final double SCALE_UP = 0x1.0p+600;




    private static final int EXP_DIFF_THRESHOLD_2D = 54;


    private final Two two;

    private final Three three;

    private final Array array;


    @FunctionalInterface
    private interface Two {





        double of(double x, double y);
    }

    @FunctionalInterface
    private interface Three {






        double of(double x, double y, double z);
    }

    @FunctionalInterface
    private interface Array {




        double of(double[] v);
    }






    Norm(Two two,
         Three three,
         Array array) {
        this.two = two;
        this.three = three;
        this.array = array;
    }




    Norm(Norm alias) {
        this.two = alias.two;
        this.three = alias.three;
        this.array = alias.array;
    }















    public double of(double x,
                     double y) {
        return two.of(x, y);
    }
















    public double of(double x,
                     double y,
                     double z) {
        return three.of(x, y, z);
    }















    public double of(double[] v) {
        ensureNonEmpty(v);
        return array.of(v);
    }











    private static double manhattan(final double x,
                                    final double y) {
        return Math.abs(x) + Math.abs(y);
    }












    private static double manhattan(final double x,
                                    final double y,
                                    final double z) {
        return Sum.of(Math.abs(x))
            .add(Math.abs(y))
            .add(Math.abs(z))
            .getAsDouble();
    }










    private static double manhattan(final double[] v) {
        final Sum sum = Sum.create();

        for (final double d : v) {
            sum.add(Math.abs(d));
        }

        return sum.getAsDouble();
    }

















    private static double euclidean(final double x,
                                    final double y) {
        final double xabs = Math.abs(x);
        final double yabs = Math.abs(y);

        final double max;
        final double min;


        if (Double.compare(xabs, yabs) > 0) {
            max = xabs;
            min = yabs;
        } else {
            max = yabs;
            min = xabs;
        }



        if (!Double.isFinite(max)) {

            return xabs * yabs;
        } else if (Math.getExponent(max) - Math.getExponent(min) > EXP_DIFF_THRESHOLD_2D) {

            return max;
        }


        final double scale;
        final double rescale;
        if (max > LARGE_THRESH) {
            scale = SCALE_DOWN;
            rescale = SCALE_UP;
        } else if (max < SAFE_SCALE_UP_THRESH) {
            scale = SCALE_UP;
            rescale = SCALE_DOWN;
        } else {
            scale = 1d;
            rescale = 1d;
        }


        final double sx = xabs * scale;
        double sum = sx * sx;
        double comp = DD.twoSquareLow(sx, sum);


        final double sy = yabs * scale;
        final double py = sy * sy;
        comp += DD.twoSquareLow(sy, py);
        final double sumPy = sum + py;
        comp += DD.twoSumLow(sum, py, sumPy);
        sum = sumPy;

        return Math.sqrt(sum + comp) * rescale;
    }













    private static double euclidean(final double x,
                                    final double y,
                                    final double z) {
        final double xabs = Math.abs(x);
        final double yabs = Math.abs(y);
        final double zabs = Math.abs(z);

        final double max = Math.max(Math.max(xabs, yabs), zabs);



        if (!Double.isFinite(max)) {


            return xabs * yabs * zabs;
        }


        final double scale;
        final double rescale;
        if (max > LARGE_THRESH) {
            scale = SCALE_DOWN;
            rescale = SCALE_UP;
        } else if (max < SAFE_SCALE_UP_THRESH) {
            scale = SCALE_UP;
            rescale = SCALE_DOWN;
        } else {
            scale = 1d;
            rescale = 1d;
        }



        final double sx = xabs * scale;
        double sum = sx * sx;
        double comp = DD.twoSquareLow(sx, sum);


        final double sy = yabs * scale;
        final double py = sy * sy;
        comp += DD.twoSquareLow(sy, py);
        final double sumPy = sum + py;
        comp += DD.twoSumLow(sum, py, sumPy);
        sum = sumPy;


        final double sz = zabs * scale;
        final double pz = sz * sz;
        comp += DD.twoSquareLow(sz, pz);
        final double sumPz = sum + pz;
        comp += DD.twoSumLow(sum, pz, sumPz);
        sum = sumPz;

        return Math.sqrt(sum + comp) * rescale;
    }











    private static double euclidean(final double[] v) {

        double s1 = 0;
        double s2 = 0;
        double s3 = 0;


        double c1 = 0;
        double c2 = 0;
        double c3 = 0;

        for (int i = 0; i < v.length; ++i) {
            final double x = Math.abs(v[i]);
            if (!Double.isFinite(x)) {

                return euclideanNormSpecial(v, i);
            } else if (x > LARGE_THRESH) {

                final double sx = x * SCALE_DOWN;


                final double p = sx * sx;
                final double cp = DD.twoSquareLow(sx, p);


                final double s = s1 + p;
                final double cs = DD.twoSumLow(s1, p, s);


                c1 += cp + cs;
                s1 = s;
            } else if (x < SMALL_THRESH) {

                final double sx = x * SCALE_UP;


                final double p = sx * sx;
                final double cp = DD.twoSquareLow(sx, p);


                final double s = s3 + p;
                final double cs = DD.twoSumLow(s3, p, s);


                c3 += cp + cs;
                s3 = s;
            } else {


                final double p = x * x;
                final double cp = DD.twoSquareLow(x, p);


                final double s = s2 + p;
                final double cs = DD.twoSumLow(s2, p, s);


                c2 += cp + cs;
                s2 = s;
            }
        }





        if (s1 != 0) {

            final double s2Adj = s2 * SCALE_DOWN * SCALE_DOWN;
            final double sum = s1 + s2Adj;
            final double comp = DD.twoSumLow(s1, s2Adj, sum) +
                c1 + (c2 * SCALE_DOWN * SCALE_DOWN);
            return Math.sqrt(sum + comp) * SCALE_UP;
        } else if (s2 != 0) {

            final double s3Adj = s3 * SCALE_DOWN * SCALE_DOWN;
            final double sum = s2 + s3Adj;
            final double comp = DD.twoSumLow(s2, s3Adj, sum) +
                c2 + (c3 * SCALE_DOWN * SCALE_DOWN);
            return Math.sqrt(sum + comp);
        }

        return Math.sqrt(s3 + c3) * SCALE_DOWN;
    }







    private static double euclideanNormSpecial(final double[] v,
                                               final int start) {
        for (int i = start; i < v.length; ++i) {
            if (Double.isNaN(v[i])) {
                return Double.NaN;
            }
        }
        return Double.POSITIVE_INFINITY;
    }











    private static double maximum(final double x,
                                  final double y) {
        return Math.max(Math.abs(x), Math.abs(y));
    }












    private static double maximum(final double x,
                                  final double y,
                                  final double z) {
        return Math.max(Math.abs(x),
                        Math.max(Math.abs(y),
                                 Math.abs(z)));
    }










    private static double maximum(final double[] v) {
        double max = 0d;
        for (final double d : v) {
            max = Math.max(max, Math.abs(d));
        }
        return max;
    }






    private static void ensureNonEmpty(double[] a) {
        if (a.length == 0) {
            throw new IllegalArgumentException("Empty array");
        }
    }
}
