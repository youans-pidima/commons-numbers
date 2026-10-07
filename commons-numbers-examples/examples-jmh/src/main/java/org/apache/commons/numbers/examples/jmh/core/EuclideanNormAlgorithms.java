















package org.apache.commons.numbers.examples.jmh.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.ToDoubleFunction;

import org.apache.commons.numbers.core.Sum;



public final class EuclideanNormAlgorithms {


    private EuclideanNormAlgorithms() {}



    static final class Exact implements ToDoubleFunction<double[]> {


        @Override
        public double applyAsDouble(final double[] v) {

            final MathContext ctx = MathContext.DECIMAL128;

            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal n;
            for (int i = 0; i < v.length; ++i) {
                n = Double.isFinite(v[i]) ? new BigDecimal(v[i]) : BigDecimal.ZERO;
                sum = sum.add(n.multiply(n, ctx), ctx);
            }

            return sum.sqrt(ctx).doubleValue();
        }
    }




    static final class Direct implements ToDoubleFunction<double[]> {


        @Override
        public double applyAsDouble(final double[] v) {
            double n = 0;
            for (int i = 0; i < v.length; i++) {
                n += v[i] * v[i];
            }
            return Math.sqrt(n);
        }
    }




    static final class Enorm implements ToDoubleFunction<double[]> {


        private static final double R_DWARF = 3.834e-20;

        private static final double R_GIANT = 1.304e+19;


        @Override
        public double applyAsDouble(final double[] v) {
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double x1max = 0;
            double x3max = 0;
            final double floatn = v.length;
            final double agiant = R_GIANT / floatn;
            for (int i = 0; i < v.length; i++) {
                final double xabs = Math.abs(v[i]);
                if (xabs < R_DWARF || xabs > agiant) {
                    if (xabs > R_DWARF) {
                        if (xabs > x1max) {
                            final double r = x1max / xabs;
                            s1 = 1 + s1 * r * r;
                            x1max = xabs;
                        } else {
                            final double r = xabs / x1max;
                            s1 += r * r;
                        }
                    } else {
                        if (xabs > x3max) {
                            final double r = x3max / xabs;
                            s3 = 1 + s3 * r * r;
                            x3max = xabs;
                        } else {
                            if (xabs != 0) {
                                final double r = xabs / x3max;
                                s3 += r * r;
                            }
                        }
                    }
                } else {
                    s2 += xabs * xabs;
                }
            }
            double norm;
            if (s1 != 0) {
                norm = x1max * Math.sqrt(s1 + (s2 / x1max) / x1max);
            } else {
                if (s2 == 0) {
                    norm = x3max * Math.sqrt(s3);
                } else {
                    if (s2 >= x3max) {
                        norm = Math.sqrt(s2 * (1 + (x3max / s2) * (x3max * s3)));
                    } else {
                        norm = Math.sqrt(x3max * ((s2 / x3max) + (x3max * s3)));
                    }
                }
            }
            return norm;
        }
    }



    static final class EnormMod implements ToDoubleFunction<double[]> {


        @Override
        public double applyAsDouble(final double[] v) {

            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (!(x <= Double.MAX_VALUE)) {
                    return x;
                } else if (x > 0x1.0p500) {

                    s1 += square(x * 0x1.0p-600);
                } else if (x < 0x1.0p-500) {

                    s3 += square(x * 0x1.0p600);
                } else {

                    s2 += square(x);
                }
            }

            if (s1 != 0) {
                return Math.sqrt(s1 + s2 * 0x1.0p-600 * 0x1.0p-600) * 0x1.0p600;
            } else if (s2 != 0) {
                return Math.sqrt(s2 + s3 * 0x1.0p-600 * 0x1.0p-600);
            }
            return Math.sqrt(s3) * 0x1.0p-600;
        }





        private static double square(final double x) {
            return x * x;
        }
    }



    static final class EnormModKahan implements ToDoubleFunction<double[]> {


        private static final double SMALL_THRESH = 0x1.0p-500;


        private static final double LARGE_THRESH = 0x1.0p+500;


        private static final double SCALE_DOWN = 0x1.0p-600;


        private static final double SCALE_UP = 0x1.0p+600;


        @Override
        public double applyAsDouble(final double[] v) {

            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (x > LARGE_THRESH) {

                    final double y = square(x * SCALE_DOWN) - c1;
                    final double t = s1 + y;
                    c1 = (t - s1) - y;
                    s1 = t;
                } else if (x < SMALL_THRESH) {

                    final double y = square(x * SCALE_UP) - c3;
                    final double t = s3 + y;
                    c3 = (t - s3) - y;
                    s3 = t;
                } else {

                    final double y = square(x) - c2;
                    final double t = s2 + y;
                    c2 = (t - s2) - y;
                    s2 = t;
                }
            }


            if (s1 != 0) {
                double y = c2 * SCALE_DOWN * SCALE_DOWN - c1;
                final double t = s1 + y;
                c1 = (t - s1) - y;
                y = s2 * SCALE_DOWN * SCALE_DOWN - c1;
                return Math.sqrt(t + y) * SCALE_UP;
            } else if (s2 != 0) {
                double y = c3 * SCALE_DOWN * SCALE_DOWN - c2;
                final double t = s2 + y;
                c2 = (t - s2) - y;
                y = s3 * SCALE_DOWN * SCALE_DOWN - c2;
                return Math.sqrt(t + y);
            }
            return Math.sqrt(s3) * SCALE_DOWN;
        }





        private static double square(final double x) {
            return x * x;
        }
    }



    static final class EnormModExt implements ToDoubleFunction<double[]> {


        private static final double SMALL_THRESH = 0x1.0p-500;


        private static final double LARGE_THRESH = 0x1.0p+500;


        private static final double SCALE_DOWN = 0x1.0p-600;


        private static final double SCALE_UP = 0x1.0p+600;


        @Override
        public double applyAsDouble(final double[] v) {

            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (!(x <= Double.MAX_VALUE)) {
                    return x;
                } else if (x > LARGE_THRESH) {

                    final double y = square(x * SCALE_DOWN);
                    final double t = s1 + y;
                    c1 += DoublePrecision.twoSumLow(s1, y, t);
                    s1 = t;
                } else if (x < SMALL_THRESH) {

                    final double y = square(x * SCALE_UP);
                    final double t = s3 + y;
                    c3 += DoublePrecision.twoSumLow(s3, y, t);
                    s3 = t;
                } else {

                    final double y = square(x);
                    final double t = s2 + y;
                    c2 += DoublePrecision.twoSumLow(s2, y, t);
                    s2 = t;
                }
            }


            if (s1 != 0) {
                s2 = s2 * SCALE_DOWN * SCALE_DOWN;
                c2 = c2 * SCALE_DOWN * SCALE_DOWN;
                double sum = s1 + s2;
                c1 += DoublePrecision.twoSumLow(s1, s2, sum) + c2;
                return Math.sqrt(sum + c1) * SCALE_UP;
            } else if (s2 != 0) {
                s3 = s3 * SCALE_DOWN * SCALE_DOWN;
                c3 = c3 * SCALE_DOWN * SCALE_DOWN;
                double sum = s2 + s3;
                c2 += DoublePrecision.twoSumLow(s2, s3, sum) + c3;
                return Math.sqrt(sum + c2);
            }
            return Math.sqrt(s3) * SCALE_DOWN;
        }





        private static double square(final double x) {
            return x * x;
        }
    }




    static final class ExtendedPrecisionLinearCombination implements ToDoubleFunction<double[]> {


        @Override
        public double applyAsDouble(final double[] v) {

            double max = 0;
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (Double.isNaN(x)) {
                    return x;
                } else if (x > max) {
                    max = x;
                } else if (x < min && x != 0) {
                    min = x;
                }
            }

            if (max == 0 || max == Double.POSITIVE_INFINITY) {
                return max;
            }

            double[] x = v;
            double rescale = 1;
            if (max > 0x1.0p500) {

                x = x.clone();
                for (int i = 0; i < x.length; i++) {
                    x[i] *= 0x1.0p-600;
                }
                rescale = 0x1.0p600;
            } else if (min < 0x1.0p-500 && max < 0x1.0p100) {

                x = x.clone();
                for (int i = 0; i < x.length; i++) {
                    x[i] *= 0x1.0p600;
                }
                rescale = 0x1.0p-600;
            }
            return Math.sqrt(Sum.ofProducts(x, x).getAsDouble()) * rescale;
        }
    }




    static final class ExtendedPrecisionLinearCombinationMod implements ToDoubleFunction<double[]> {


        private static final double SMALL_THRESH = 0x1.0p-500;


        private static final double LARGE_THRESH = 0x1.0p+500;


        private static final double SCALE_DOWN = 0x1.0p-600;


        private static final double SCALE_UP = 0x1.0p+600;


        @Override
        public double applyAsDouble(final double[] v) {

            double max = 0;
            double min = Double.POSITIVE_INFINITY;
            for (int i = 0; i < v.length; i++) {
                final double x = Math.abs(v[i]);
                if (Double.isNaN(x)) {
                    return x;
                } else if (x > max) {
                    max = x;
                } else if (x < min && x != 0) {
                    min = x;
                }
            }

            if (max == 0 || max == Double.POSITIVE_INFINITY) {
                return max;
            }




            double scale = 1;
            double rescale = 1;
            if (max > LARGE_THRESH) {

                scale = SCALE_DOWN;
                rescale = SCALE_UP;
            } else if (min < SMALL_THRESH && max < 0x1.0p100) {

                scale = SCALE_UP;
                rescale = SCALE_DOWN;
            }





            final int len = v.length;



            double a = v[0] * scale;
            double p = a * a;
            double s = productLowUnscaled(a, p);


            for (int i = 1; i < len; i++) {
                a = v[i] * scale;
                final double h = a * a;
                final double r = productLowUnscaled(a, h);

                final double x = p + h;

                s += DoublePrecision.twoSumLow(p, h, x) + r;
                p = x;
            }
            p += s;

            return Math.sqrt(p) * rescale;
        }














        private static double productLowUnscaled(double x, double xx) {

            final double hx = DoublePrecision.highPartUnscaled(x);
            final double lx = x - hx;

            return DoublePrecision.productLow(hx, lx, hx, lx, xx);
        }
    }




    static final class ExtendedPrecisionLinearCombinationSinglePass implements ToDoubleFunction<double[]> {


        private static final double SMALL_THRESH = 0x1.0p-500;


        private static final double LARGE_THRESH = 0x1.0p+500;


        private static final double SCALE_DOWN = 0x1.0p-600;


        private static final double SCALE_UP = 0x1.0p+600;


        @Override
        public double applyAsDouble(final double[] v) {
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; ++i) {
                final double x = Math.abs(v[i]);
                if (Double.isNaN(x)) {

                    return x;
                } else if (x > LARGE_THRESH) {

                    final double sx = x * SCALE_DOWN;


                    final double p = sx * sx;
                    final double cp = productLowUnscaled(sx, p);


                    final double s = s1 + p;
                    final double cs = DoublePrecision.twoSumLow(s1, p, s);


                    c1 += cp + cs;
                    s1 = s;
                } else if (x < SMALL_THRESH) {

                    final double sx = x * SCALE_UP;


                    final double p = sx * sx;
                    final double cp = productLowUnscaled(sx, p);


                    final double s = s3 + p;
                    final double cs = DoublePrecision.twoSumLow(s3, p, s);


                    c3 += cp + cs;
                    s3 = s;
                } else {


                    final double p = x * x;
                    final double cp = productLowUnscaled(x, p);


                    final double s = s2 + p;
                    final double cs = DoublePrecision.twoSumLow(s2, p, s);


                    c2 += cp + cs;
                    s2 = s;
                }
            }

            if (s1 != 0) {

                s2 = s2 * SCALE_DOWN * SCALE_DOWN;
                c2 = c2 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s1 + s2;
                c1 += DoublePrecision.twoSumLow(s1, s2, sum) + c2;
                return Math.sqrt(sum + c1) * SCALE_UP;
            } else if (s2 != 0) {

                s3 = s3 * SCALE_DOWN * SCALE_DOWN;
                c3 = c3 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s2 + s3;
                c2 += DoublePrecision.twoSumLow(s2, s3, sum) + c3;
                return Math.sqrt(sum + c2);
            }

            return Math.sqrt(s3 + c3) * SCALE_DOWN;
        }














        private static double productLowUnscaled(double x, double xx) {

            final double hx = DoublePrecision.highPartUnscaled(x);
            final double lx = x - hx;

            return DoublePrecision.productLow(hx, lx, hx, lx, xx);
        }
    }




    static final class ExtendedPrecisionLinearCombinationSqrt2 implements ToDoubleFunction<double[]> {


        private static final double SMALL_THRESH = 0x1.0p-500;


        private static final double LARGE_THRESH = 0x1.0p+500;


        private static final double SCALE_DOWN = 0x1.0p-600;


        private static final double SCALE_UP = 0x1.0p+600;


        @Override
        public double applyAsDouble(final double[] v) {
            double s1 = 0;
            double s2 = 0;
            double s3 = 0;
            double c1 = 0;
            double c2 = 0;
            double c3 = 0;
            for (int i = 0; i < v.length; ++i) {
                final double x = Math.abs(v[i]);
                if (Double.isNaN(x)) {

                    return x;
                } else if (x > LARGE_THRESH) {

                    final double sx = x * SCALE_DOWN;


                    final double p = sx * sx;
                    final double cp = productLowUnscaled(sx, p);


                    final double s = s1 + p;
                    final double cs = DoublePrecision.twoSumLow(s1, p, s);


                    c1 += cp + cs;
                    s1 = s;
                } else if (x < SMALL_THRESH) {

                    final double sx = x * SCALE_UP;


                    final double p = sx * sx;
                    final double cp = productLowUnscaled(sx, p);


                    final double s = s3 + p;
                    final double cs = DoublePrecision.twoSumLow(s3, p, s);


                    c3 += cp + cs;
                    s3 = s;
                } else {


                    final double p = x * x;
                    final double cp = productLowUnscaled(x, p);


                    final double s = s2 + p;
                    final double cs = DoublePrecision.twoSumLow(s2, p, s);


                    c2 += cp + cs;
                    s2 = s;
                }
            }

            if (s1 != 0) {

                s2 = s2 * SCALE_DOWN * SCALE_DOWN;
                c2 = c2 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s1 + s2;
                c1 += DoublePrecision.twoSumLow(s1, s2, sum) + c2;

                final double f = sum + c1;
                final double ff = DoublePrecision.twoSumLow(sum, c1, f);
                return sqrt2(f, ff) * SCALE_UP;
            } else if (s2 != 0) {

                s3 = s3 * SCALE_DOWN * SCALE_DOWN;
                c3 = c3 * SCALE_DOWN * SCALE_DOWN;
                final double sum = s2 + s3;
                c2 += DoublePrecision.twoSumLow(s2, s3, sum) + c3;

                final double f = sum + c2;
                final double ff = DoublePrecision.twoSumLow(sum, c2, f);
                return sqrt2(f, ff);
            }

            final double f = s3 + c3;
            final double ff = DoublePrecision.twoSumLow(s3, c3, f);
            return sqrt2(f, ff) * SCALE_DOWN;
        }














        private static double productLowUnscaled(double x, double xx) {

            final double hx = DoublePrecision.highPartUnscaled(x);
            final double lx = x - hx;

            return DoublePrecision.productLow(hx, lx, hx, lx, xx);
        }











        private static double sqrt2(final double x, final double xx) {
            if (x > 0) {
                double c = Math.sqrt(x);
                double u = c * c;


                double uu = productLowUnscaled(c, u);
                double cc = (x - u - uu + xx) * 0.5 / c;



                return c + cc;
            }
            return x;
        }
    }
}
