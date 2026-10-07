















package org.apache.commons.numbers.examples.jmh.core;

import java.math.BigDecimal;
import java.util.function.IntFunction;

import org.apache.commons.numbers.examples.jmh.core.LinearCombination.FourD;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.ND;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.ThreeD;
import org.apache.commons.numbers.examples.jmh.core.LinearCombination.TwoD;








public final class LinearCombinations {


    private LinearCombinations() {}






    public abstract static class BaseLinearCombination implements LinearCombination.ND {

        @Override
        public double value(double[] a, double[] b) {
            if (a.length != b.length) {
                throw new IllegalArgumentException("Dimension mismatch: " + a.length + " != " + b.length);
            }

            final int len = a.length;

            if (len == 0) {
                return 0;
            }
            if (len == 1) {

                return a[0] * b[0];
            }

            return computeValue(a, b);
        }










        protected abstract double computeValue(double[] a, double[] b);









        static double standardDotProduct(double[] a, double[] b) {
            double result = 0;
            for (int i = 0; i < a.length; ++i) {
                result += a[i] * b[i];
            }
            return result;
        }
    }






    static final class StandardPrecision implements TwoD, ThreeD, FourD, ND {

        static final StandardPrecision INSTANCE = new StandardPrecision();


        private StandardPrecision() {}

        @Override
        public double value(double[] a, double[] b) {
            double sum = 0;
            for (int i = 0; i < a.length; i++) {
                sum += a[i] * b[i];
            }
            return sum;
        }

        @Override
        public double value(double a1, double b1, double a2, double b2) {
            return a1 * b1 + a2 * b2;
        }

        @Override
        public double value(double a1, double b1, double a2, double b2, double a3, double b3) {
            return a1 * b1 + a2 * b2 + a3 * b3;
        }

        @Override
        public double value(double a1, double b1, double a2, double b2, double a3, double b3, double a4, double b4) {
            return a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4;
        }
    }








    public static final class Dekker extends BaseLinearCombination implements TwoD, ThreeD, FourD {

        public static final Dekker INSTANCE = new Dekker();


        private Dekker() {}

        @Override
        protected double computeValue(double[] a, double[] b) {



            double x = a[0] * b[0];
            double xx = DoublePrecision.productLow(a[0], b[0], x);


            for (int i = 1; i < a.length; i++) {
                final double y = a[i] * b[i];
                final double yy = DoublePrecision.productLow(a[i], b[i], y);


                final double r = x + y;
                final double s = DoublePrecision.sumLow(x, xx, y, yy, r);

                x = r + s;

                xx = r - x + s;
            }

            if (!Double.isFinite(x)) {


                return standardDotProduct(a, b);
            }
            return x;
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2) {
            final double x = a1 * b1;
            final double xx = DoublePrecision.productLow(a1, b1, x);
            final double y = a2 * b2;
            final double yy = DoublePrecision.productLow(a2, b2, y);
            double r = x + y;
            final double s = DoublePrecision.sumLow(x, xx, y, yy, r);
            r = r + s;

            if (!Double.isFinite(r)) {


                return x + y;
            }
            return r;
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3) {
            double x = a1 * b1;
            double xx = DoublePrecision.productLow(a1, b1, x);
            double y = a2 * b2;
            double yy = DoublePrecision.productLow(a2, b2, y);
            double r = x + y;
            double s = DoublePrecision.sumLow(x, xx, y, yy, r);
            x = r + s;
            xx = r - x + s;
            y = a3 * b3;
            yy = DoublePrecision.productLow(a3, b3, y);
            r = x + y;
            s = DoublePrecision.sumLow(x, xx, y, yy, r);
            x = r + s;

            if (!Double.isFinite(x)) {


                return a1 * b1 + a2 * b2 + y;
            }
            return x;
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3,
                            double a4, double b4) {
            double x = a1 * b1;
            double xx = DoublePrecision.productLow(a1, b1, x);
            double y = a2 * b2;
            double yy = DoublePrecision.productLow(a2, b2, y);
            double r = x + y;
            double s = DoublePrecision.sumLow(x, xx, y, yy, r);
            x = r + s;
            xx = r - x + s;
            y = a3 * b3;
            yy = DoublePrecision.productLow(a3, b3, y);
            r = x + y;
            s = DoublePrecision.sumLow(x, xx, y, yy, r);
            x = r + s;
            xx = r - x + s;
            y = a4 * b4;
            yy = DoublePrecision.productLow(a4, b4, y);
            r = x + y;
            s = DoublePrecision.sumLow(x, xx, y, yy, r);
            x = r + s;

            if (!Double.isFinite(x)) {


                return a1 * b1 + a2 * b2 + a3 * b3 + y;
            }
            return x;
        }
    }




























    public static final class DotK extends BaseLinearCombination implements TwoD, ThreeD, FourD {

        public static final DotK DOT_3 = new DotK(3);

        public static final DotK DOT_4 = new DotK(4);

        public static final DotK DOT_5 = new DotK(5);

        public static final DotK DOT_6 = new DotK(6);

        public static final DotK DOT_7 = new DotK(7);


        private final int k;

        private final IntFunction<double[]> arrayFactory;






        public DotK(int k) {
            this(k, double[]::new);
        }







        DotK(int k, IntFunction<double[]> arrayFactory) {
            this.k = k;
            this.arrayFactory = arrayFactory;
        }


        @Override
        protected double computeValue(double[] a, double[] b) {





            final int len = a.length;
            final double[] r = arrayFactory.apply(len * 2);


            double p = a[0] * b[0];
            r[0] = DoublePrecision.productLow(a[0], b[0], p);


            for (int i = 1; i < len; i++) {
                final double h = a[i] * b[i];
                r[i] = DoublePrecision.productLow(a[i], b[i], h);

                final double x = p + h;
                r[i + len - 1] = DoublePrecision.twoSumLow(p, h, x);
                p = x;
            }





            r[r.length - 1] = p;
            return getSum(p, sumK(r, k - 1));
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2) {





            double p = a1 * b1;
            double r0 = DoublePrecision.productLow(a1, b1, p);
            double q = a2 * b2;
            double r1 = DoublePrecision.productLow(a2, b2, q);
            final double s = p + q;
            double r2 = DoublePrecision.twoSumLow(p, q, s);
            double r3 = s;


            for (int i = 2; i < k; i++) {
                q = r1 + r0;
                r0 = DoublePrecision.twoSumLow(r1, r0, q);
                p = r2 + q;
                r1 = DoublePrecision.twoSumLow(r2, q, p);
                q = r3 + p;
                r2 = DoublePrecision.twoSumLow(r3, p, q);
                r3 = q;
            }


            return getSum(s, r0 + r1 + r2 + r3);
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3) {





            double p = a1 * b1;
            double r0 = DoublePrecision.productLow(a1, b1, p);
            double h = a2 * b2;
            double r1 = DoublePrecision.productLow(a2, b2, h);
            double q = p + h;
            double r3 = DoublePrecision.twoSumLow(p, h, q);
            h = a3 * b3;
            double r2 = DoublePrecision.productLow(a3, b3, h);
            final double s = q + h;
            double r4 = DoublePrecision.twoSumLow(q, h, s);
            double r5 = s;


            for (int i = 2; i < k; i++) {
                q = r1 + r0;
                r0 = DoublePrecision.twoSumLow(r1, r0, q);
                p = r2 + q;
                r1 = DoublePrecision.twoSumLow(r2, q, p);
                q = r3 + p;
                r2 = DoublePrecision.twoSumLow(r3, p, q);
                p = r4 + q;
                r3 = DoublePrecision.twoSumLow(r4, q, p);
                q = r5 + p;
                r4 = DoublePrecision.twoSumLow(r5, p, q);
                r5 = q;
            }


            return getSum(s, r0 + r1 + r2 + r3 + r4 + r5);
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3,
                            double a4, double b4) {





            double p = a1 * b1;
            double r0 = DoublePrecision.productLow(a1, b1, p);
            double h = a2 * b2;
            double r1 = DoublePrecision.productLow(a2, b2, h);
            double q = p + h;
            double r4 = DoublePrecision.twoSumLow(p, h, q);
            h = a3 * b3;
            double r2 = DoublePrecision.productLow(a3, b3, h);
            p = q + h;
            double r5 = DoublePrecision.twoSumLow(q, h, p);
            h = a4 * b4;
            double r3 = DoublePrecision.productLow(a4, b4, h);
            final double s = p + h;
            double r6 = DoublePrecision.twoSumLow(p, h, s);
            double r7 = s;


            for (int i = 2; i < k; i++) {
                q = r1 + r0;
                r0 = DoublePrecision.twoSumLow(r1, r0, q);
                p = r2 + q;
                r1 = DoublePrecision.twoSumLow(r2, q, p);
                q = r3 + p;
                r2 = DoublePrecision.twoSumLow(r3, p, q);
                p = r4 + q;
                r3 = DoublePrecision.twoSumLow(r4, q, p);
                q = r5 + p;
                r4 = DoublePrecision.twoSumLow(r5, p, q);
                p = r6 + q;
                r5 = DoublePrecision.twoSumLow(r6, q, p);
                q = r7 + p;
                r6 = DoublePrecision.twoSumLow(r7, p, q);
                r7 = q;
            }


            return getSum(s, r0 + r1 + r2 + r3 + r4 + r5 + r6 + r7);
        }








        private static double sumK(double[] p, int km1) {

            for (int i = 1; i < km1; i++) {
                vectorSum(p);
            }
            double sum = 0;
            for (final double pi : p) {
                sum += pi;
            }
            return sum;
        }






        private static void vectorSum(double[] p) {
            for (int i = 1; i < p.length; i++) {
                final double x = p[i] + p[i - 1];
                p[i - 1] = DoublePrecision.twoSumLow(p[i], p[i - 1], x);
                p[i] = x;
            }
        }
    }













    public static final class Dot2s extends BaseLinearCombination implements TwoD, ThreeD, FourD {

        public static final Dot2s INSTANCE = new Dot2s();


        private Dot2s() {}


        @Override
        protected double computeValue(double[] a, double[] b) {

            final int len = a.length;



            double p = a[0] * b[0];
            double s = DoublePrecision.productLow(a[0], b[0], p);


            for (int i = 1; i < len; i++) {
                final double h = a[i] * b[i];
                final double r = DoublePrecision.productLow(a[i], b[i], h);

                final double x = p + h;

                s += DoublePrecision.twoSumLow(p, h, x) + r;
                p = x;
            }

            return getSum(p, p + s);
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2) {


            final double p = a1 * b1;
            double s = DoublePrecision.productLow(a1, b1, p);
            final double h = a2 * b2;
            final double r = DoublePrecision.productLow(a2, b2, h);
            final double pn = p + h;
            s += DoublePrecision.twoSumLow(p, h, pn) + r;


            return getSum(pn, pn + s);
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3) {


            final double p = a1 * b1;
            double s = DoublePrecision.productLow(a1, b1, p);
            double h = a2 * b2;
            double r = DoublePrecision.productLow(a2, b2, h);
            final double q = p + h;
            s += r + DoublePrecision.twoSumLow(p, h, q);
            h = a3 * b3;
            r = DoublePrecision.productLow(a3, b3, h);
            final double pn = q + h;
            s += r + DoublePrecision.twoSumLow(q, h, pn);


            return getSum(pn, pn + s);
        }


        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3,
                            double a4, double b4) {



            double p = a1 * b1;
            double s = DoublePrecision.productLow(a1, b1, p);
            double h = a2 * b2;
            double r = DoublePrecision.productLow(a2, b2, h);
            final double q = p + h;
            s += DoublePrecision.twoSumLow(p, h, q) + r;
            h = a3 * b3;
            r = DoublePrecision.productLow(a3, b3, h);
            p = q + h;
            s += DoublePrecision.twoSumLow(q, h, p) + r;
            h = a4 * b4;
            r = DoublePrecision.productLow(a4, b4, h);
            final double pn = p + h;
            s += DoublePrecision.twoSumLow(p, h, pn) + r;


            return getSum(pn, pn + s);
        }
    }





    public static final class Exact extends BaseLinearCombination implements TwoD, ThreeD, FourD {

        public static final Exact INSTANCE = new Exact();


        private Exact() {}

        @Override
        protected double computeValue(double[] a, double[] b) {

            if (!areFinite(a) || !areFinite(b)) {
                return standardDotProduct(a, b);
            }
            BigDecimal sum = multiply(a[0], b[0]);
            for (int i = 1; i < a.length; i++) {
                sum = sum.add(multiply(a[i], b[i]));
            }
            return sum.doubleValue();
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2) {

            if (!areFinite(a1, b1, a2, b2)) {
                return a1 * b1 + a2 * b2;
            }
            return multiply(a1, b1)
                    .add(multiply(a2, b2))
                    .doubleValue();
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3) {

            if (!areFinite(a1, b1, a2, b2, a3, b3)) {
                return a1 * b1 + a2 * b2 + a3 * b3;
            }
            return multiply(a1, b1)
                    .add(multiply(a2, b2))
                    .add(multiply(a3, b3))
                    .doubleValue();
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3,
                            double a4, double b4) {

            if (!areFinite(a1, b1, a2, b2, a3, b3, a4, b4)) {
                return a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4;
            }
            return multiply(a1, b1)
                    .add(multiply(a2, b2))
                    .add(multiply(a3, b3))
                    .add(multiply(a4, b4))
                    .doubleValue();
        }







        private static boolean areFinite(double... values) {
            for (final double value : values) {
                if (!Double.isFinite(value)) {
                    return false;
                }
            }
            return true;
        }








        private static BigDecimal multiply(double a, double b) {
            return new BigDecimal(a).multiply(new BigDecimal(b));
        }
    }









    public static final class ExtendedPrecision extends BaseLinearCombination implements TwoD, ThreeD, FourD {
























        public static final ExtendedPrecision INSTANCE = new ExtendedPrecision(ApproximationSum.INSTANCE);





        public static final ExtendedPrecision DOUBLE = new ExtendedPrecision(TwoSum.INSTANCE);




        public static final ExtendedPrecision EXACT = new ExtendedPrecision(ExactSum.INSTANCE);




        public static final ExtendedPrecision EXACT2 = new ExtendedPrecision(ExactSum2.INSTANCE);




        private interface ExpansionSum {







            double sum(double[] e, int m);










            double sum(double e0, double e1, double e2, double e3);












            double sum(double e0, double e1, double e2, double e3, double e4, double e5);














            double sum(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7);
        }





        private static final class ApproximationSum implements ExpansionSum {

            static final ApproximationSum INSTANCE = new ApproximationSum();


            private ApproximationSum() {}

            @Override
            public double sum(double[] e, int m) {
                double sum = 0;
                for (int i = 0; i < m; i++) {
                    sum += e[i];
                }
                return sum;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3) {
                return e0 + e1 + e2 + e3;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5) {
                return e0 + e1 + e2 + e3 + e4 + e5;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7) {
                return e0 + e1 + e2 + e3 + e4 + e5 + e6 + e7;
            }
        }







        private static final class TwoSum implements ExpansionSum {

            static final TwoSum INSTANCE = new TwoSum();


            private TwoSum() {}

            @Override
            public double sum(double[] e, int m) {
                double q = e[0];
                double qq = 0.0;
                for (int i = 1; i < m; i++) {
                    final double p = q + e[i];
                    qq += DoublePrecision.twoSumLow(q, e[i], p);
                    q = p;
                }
                return q + qq;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3) {
                double q = e0 + e1;
                double qq = DoublePrecision.fastTwoSumLow(e0, e1, q);
                final double p = q + e2;
                qq += DoublePrecision.twoSumLow(q, e2, p);
                q = p + e3;
                qq += DoublePrecision.twoSumLow(p, e3, q);
                return q + qq;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5) {
                double q = e0 + e1;
                double qq = DoublePrecision.fastTwoSumLow(e0, e1, q);
                double p = q + e2;
                qq += DoublePrecision.twoSumLow(q, e2, p);
                q = p + e3;
                qq += DoublePrecision.twoSumLow(p, e3, q);
                p = q + e4;
                qq += DoublePrecision.twoSumLow(q, e4, p);
                q = p + e5;
                qq += DoublePrecision.twoSumLow(p, e5, q);
                return q + qq;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7) {
                double q = e0 + e1;
                double qq = DoublePrecision.fastTwoSumLow(e0, e1, q);
                double p = q + e2;
                qq += DoublePrecision.twoSumLow(q, e2, p);
                q = p + e3;
                qq += DoublePrecision.twoSumLow(p, e3, q);
                p = q + e4;
                qq += DoublePrecision.twoSumLow(q, e4, p);
                q = p + e5;
                qq += DoublePrecision.twoSumLow(p, e5, q);
                p = q + e6;
                qq += DoublePrecision.twoSumLow(q, e6, p);
                q = p + e7;
                qq += DoublePrecision.twoSumLow(p, e7, q);
                return q + qq;
            }
        }




























        private static class ExactSum implements ExpansionSum {

            static final ExactSum INSTANCE = new ExactSum();


            ExactSum() {}

            @Override
            public double sum(double[] e, int size) {
                if (size == 1) {
                    return e[0];
                }




                final int m = size - 1;
                double q = e[m];
                int bottom = m;
                for (int i = m - 1; i >= 0; i--) {
                    final double p = q + e[i];
                    final double qq = DoublePrecision.fastTwoSumLow(q, e[i], p);
                    if (qq != 0) {

                        e[bottom] = p;
                        bottom--;
                        q = qq;
                    } else {

                        q = p;
                    }
                }



                if (bottom == m) {

                    return q;
                }




                for (int i = bottom + 1; i < m; i++) {
                    q = fastSumWithStickyBit(e[i], q);
                }

                return e[m] + q;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3) {

                double g3 = 0.0;
                double g2 = 0.0;
                double g1 = 0.0;


                double q = e3 + e2;
                double qq = DoublePrecision.fastTwoSumLow(e3, e2, q);
                if (qq != 0) {
                    g3 = q;
                    q = qq;
                }
                double p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    g2 = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    g1 = p;
                    q = qq;
                }





                return stickySum(q, g1, g2, g3);
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5) {

                double g5 = 0.0;
                double g4 = 0.0;
                double g3 = 0.0;
                double g2 = 0.0;
                double g1 = 0.0;


                double q = e5 + e4;
                double qq = DoublePrecision.fastTwoSumLow(e5, e4, q);
                if (qq != 0) {
                    g5 = q;
                    q = qq;
                }
                double p = q + e3;
                qq = DoublePrecision.fastTwoSumLow(q, e3, p);
                if (qq != 0) {
                    g4 = p;
                    p = qq;
                }
                q = p + e2;
                qq = DoublePrecision.fastTwoSumLow(p, e2, q);
                if (qq != 0) {
                    g3 = q;
                    q = qq;
                }
                p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    g2 = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    g1 = q;
                    q = qq;
                }





                return stickySum(q, g1, g2, g3, g4, g5);
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7) {

                double g7 = 0.0;
                double g6 = 0.0;
                double g5 = 0.0;
                double g4 = 0.0;
                double g3 = 0.0;
                double g2 = 0.0;
                double g1 = 0.0;






                double q = e7 + e6;
                double qq = DoublePrecision.fastTwoSumLow(e7, e6, q);
                if (qq != 0) {
                    g7 = q;
                    q = qq;
                }
                double p = q + e5;
                qq = DoublePrecision.fastTwoSumLow(q, e5, p);
                if (qq != 0) {
                    g6 = p;
                    p = qq;
                }
                q = p + e4;
                qq = DoublePrecision.fastTwoSumLow(p, e4, q);
                if (qq != 0) {
                    g5 = q;
                    q = qq;
                }
                p = q + e3;
                qq = DoublePrecision.fastTwoSumLow(q, e3, p);
                if (qq != 0) {
                    g4 = p;
                    p = qq;
                }
                q = p + e2;
                qq = DoublePrecision.fastTwoSumLow(p, e2, q);
                if (qq != 0) {
                    g3 = q;
                    q = qq;
                }
                p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    g2 = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    g1 = q;
                    q = qq;
                }





                return stickySum(q, g1, g2, g3, g4, g5, g6, g7);
            }
















            private static double stickySum(double g0, double g1, double g2, double g3,
                                            double g4, double g5, double g6, double g7) {
                if (g7 == 0) {
                    return stickySum(g0, g1, g2, g3, g4, g5, g6);
                }
                double q = fastSumWithStickyBit(g1, g0);
                q = fastSumWithStickyBit(g2, q);
                q = fastSumWithStickyBit(g3, q);
                q = fastSumWithStickyBit(g4, q);
                q = fastSumWithStickyBit(g5, q);
                return g7 + fastSumWithStickyBit(g6, q);
            }















            private static double stickySum(double g0, double g1, double g2, double g3,
                                            double g4, double g5, double g6) {
                if (g6 == 0) {
                    return stickySum(g0, g1, g2, g3, g4, g5);
                }
                double q = fastSumWithStickyBit(g1, g0);
                q = fastSumWithStickyBit(g2, q);
                q = fastSumWithStickyBit(g3, q);
                q = fastSumWithStickyBit(g4, q);
                return g6 + fastSumWithStickyBit(g5, q);
            }














            private static double stickySum(double g0, double g1, double g2, double g3,
                                            double g4, double g5) {
                if (g5 == 0) {
                    return stickySum(g0, g1, g2, g3, g4);
                }
                double q = fastSumWithStickyBit(g1, g0);
                q = fastSumWithStickyBit(g2, q);
                q = fastSumWithStickyBit(g3, q);
                return g5 + fastSumWithStickyBit(g4, q);
            }













            private static double stickySum(double g0, double g1, double g2, double g3,
                                            double g4) {
                if (g4 == 0) {
                    return stickySum(g0, g1, g2, g3);
                }
                double q = fastSumWithStickyBit(g1, g0);
                q = fastSumWithStickyBit(g2, q);
                return g4 + fastSumWithStickyBit(g3, q);
            }












            private static double stickySum(double g0, double g1, double g2, double g3) {
                if (g3 == 0) {
                    return stickySum(g0, g1, g2);
                }
                final double q = fastSumWithStickyBit(g1, g0);
                return g3 + fastSumWithStickyBit(g2, q);
            }











            private static double stickySum(double g0, double g1, double g2) {
                if (g2 == 0) {

                    return g1 + g0;
                }
                return g2 + fastSumWithStickyBit(g1, g0);
            }























            static double fastSumWithStickyBit(double a, double b) {
                double sum = a + b;


                final double r = b - (sum - a);

                if (r != 0) {




















                    long hi = Double.doubleToRawLongBits(sum);

                    if ((hi & 0x1) == 0) {




                        if (sum > 0) {
                            hi += (r > 0) ? 1 : -1;
                        } else {
                            hi += (r < 0) ? 1 : -1;
                        }
                        sum = Double.longBitsToDouble(hi);
                    }
                }
                return sum;
            }
        }









        private static final class ExactSum2 extends ExactSum {

            static final ExactSum2 INSTANCE = new ExactSum2();


            private ExactSum2() {}

            @Override
            public double sum(double[] e, int size) {
                if (size == 1) {
                    return e[0];
                }




                final int m = size - 1;
                double q = e[m];
                int bottom = m;
                for (int i = m - 1; i >= 0; i--) {
                    final double p = q + e[i];
                    final double qq = DoublePrecision.fastTwoSumLow(q, e[i], p);
                    if (qq != 0) {

                        e[bottom] = p;
                        bottom--;
                        q = qq;
                    } else {

                        q = p;
                    }
                }



                return stickySum(q, e, bottom, m);
            }












            private static double stickySum(double q, double[] e, int bottom, int m) {
                if (bottom == m) {

                    return q;
                }




                for (int i = bottom + 1; i < m; i++) {
                    q = fastSumWithStickyBit(e[i], q);
                }

                return e[m] + q;
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3) {
                int bottom = 2;
                final double[] e = new double[3];


                double q = e3 + e2;
                double qq = DoublePrecision.fastTwoSumLow(e3, e2, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                double p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }

                return stickySum(q, e, bottom, 2);
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5) {
                int bottom = 4;
                final double[] e = new double[5];


                double q = e5 + e4;
                double qq = DoublePrecision.fastTwoSumLow(e5, e4, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                double p = q + e3;
                qq = DoublePrecision.fastTwoSumLow(q, e3, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e2;
                qq = DoublePrecision.fastTwoSumLow(p, e2, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }

                return stickySum(q, e, bottom, 4);
            }

            @Override
            public double sum(double e0, double e1, double e2, double e3, double e4, double e5, double e6, double e7) {
                int bottom = 6;
                final double[] e = new double[7];






                double q = e7 + e6;
                double qq = DoublePrecision.fastTwoSumLow(e7, e6, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                double p = q + e5;
                qq = DoublePrecision.fastTwoSumLow(q, e5, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e4;
                qq = DoublePrecision.fastTwoSumLow(p, e4, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                p = q + e3;
                qq = DoublePrecision.fastTwoSumLow(q, e3, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e2;
                qq = DoublePrecision.fastTwoSumLow(p, e2, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }
                p = q + e1;
                qq = DoublePrecision.fastTwoSumLow(q, e1, p);
                if (qq != 0) {
                    e[bottom--] = p;
                    p = qq;
                }
                q = p + e0;
                qq = DoublePrecision.fastTwoSumLow(p, e0, q);
                if (qq != 0) {
                    e[bottom--] = q;
                    q = qq;
                }

                return stickySum(q, e, bottom, 6);
            }
        }




        enum Summation {

            STANDARD,

            DOUBLE,

            EXACT
        }


        private final ExpansionSum summation;

        private final IntFunction<double[]> arrayFactory;






        private ExtendedPrecision(ExpansionSum summation) {
            this(summation, double[]::new);
        }







        private ExtendedPrecision(ExpansionSum summation, IntFunction<double[]> arrayFactory) {
            this.summation = summation;
            this.arrayFactory = arrayFactory;
        }









        static ExtendedPrecision of(Summation type, IntFunction<double[]> arrayFactory) {
            switch (type) {
            case DOUBLE:
                return new ExtendedPrecision(TwoSum.INSTANCE);
            case EXACT:
                return new ExtendedPrecision(ExactSum.INSTANCE);
            case STANDARD:
            default:
                return new ExtendedPrecision(ApproximationSum.INSTANCE);
            }
        }

        @Override
        protected double computeValue(double[] a, double[] b) {





            final int len = a.length;
            final double[] e = arrayFactory.apply(len * 2);
            int size = sumProduct(a[0], b[0], a[1], b[1], e);



            for (int i = 3; i < len; i += 2) {



                final double a1 = a[i - 1];
                final double b1 = b[i - 1];
                final double a2 = a[i];
                final double b2 = b[i];


                double e1 = a1 * b1;
                double e0 = DoublePrecision.productLow(a1, b1, e1);


                final double f1 = a2 * b2;
                final double f0 = DoublePrecision.productLow(a2, b2, f1);



                double q = e0 + f0;
                e0 = DoublePrecision.twoSumLow(e0, f0, q);
                double e2 = e1 + q;
                e1 = DoublePrecision.twoSumLow(e1, q, e2);

                q = e1 + f1;
                e1 = DoublePrecision.twoSumLow(e1, f1, q);
                final double e3 = e2 + q;
                e2 = DoublePrecision.twoSumLow(e2, q, e3);


                int n = 0;
                if (e0 != 0) {
                    growExpansion(e, size++, n++, e0);
                }
                if (e1 != 0) {
                    growExpansion(e, size++, n++, e1);
                }
                if (e2 != 0) {
                    growExpansion(e, size++, n++, e2);
                }


                growExpansion(e, size++, n, e3);

                size = zeroElimination(e, size);
            }

            if ((len & 0x1) == 0x1) {

                final int i = len - 1;
                final double a1 = a[i];
                final double b1 = b[i];
                final double f1 = a1 * b1;
                final double f0 = DoublePrecision.productLow(a1, b1, f1);
                if (f0 != 0) {
                    growExpansion(e, size++, 0, f0);
                    growExpansion(e, size++, 1, f1);
                } else {
                    growExpansion(e, size++, 0, f1);
                }

            }


            if (size == 0) {
                return 0.0;
            }

            final double result = summation.sum(e, size);
            if (!Double.isFinite(result)) {


                return standardDotProduct(a, b);
            }
            return result;
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2) {

            double s;


            double e1 = a1 * b1;
            double e0 = DoublePrecision.productLow(a1, b1, e1);
            s = e1;


            final double f1 = a2 * b2;
            final double f0 = DoublePrecision.productLow(a2, b2, f1);
            s += f1;


            double q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double e2 = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, e2);

            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            final double e3 = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, e3);


            return getSum(s, summation.sum(e0, e1, e2, e3));
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3) {

            double s;


            double e1 = a1 * b1;
            double e0 = DoublePrecision.productLow(a1, b1, e1);
            s = e1;


            double f1 = a2 * b2;
            double f0 = DoublePrecision.productLow(a2, b2, f1);
            s += f1;


            double q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double e2 = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, e2);

            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            double e3 = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, e3);


            f1 = a3 * b3;
            f0 = DoublePrecision.productLow(a3, b3, f1);
            s += f1;



            q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double p = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, p);
            q = e2 + p;
            e2 = DoublePrecision.twoSumLow(e2, p, q);
            double e4 = e3 + q;
            e3 = DoublePrecision.twoSumLow(e3, q, e4);


            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            p = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, p);
            q = e3 + p;
            e3 = DoublePrecision.twoSumLow(e3, p, q);
            final double e5 = e4 + q;
            e4 = DoublePrecision.twoSumLow(e4, q, e5);


            return getSum(s, summation.sum(e0, e1, e2, e3, e4, e5));
        }

        @Override
        public double value(double a1, double b1,
                            double a2, double b2,
                            double a3, double b3,
                            double a4, double b4) {

            double s;


            double e1 = a1 * b1;
            double e0 = DoublePrecision.productLow(a1, b1, e1);
            s = e1;


            double f1 = a2 * b2;
            double f0 = DoublePrecision.productLow(a2, b2, f1);
            s += f1;


            double q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double e2 = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, e2);

            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            double e3 = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, e3);


            f1 = a3 * b3;
            f0 = DoublePrecision.productLow(a3, b3, f1);
            s += f1;


            final double g1 = a4 * b4;
            final double g0 = DoublePrecision.productLow(a4, b4, g1);
            s += g1;


            q = f0 + g0;
            f0 = DoublePrecision.twoSumLow(f0, g0, q);
            double f2 = f1 + q;
            f1 = DoublePrecision.twoSumLow(f1, q, f2);

            q = f1 + g1;
            f1 = DoublePrecision.twoSumLow(f1, g1, q);
            final double f3 = f2 + q;
            f2 = DoublePrecision.twoSumLow(f2, q, f3);



            q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double p = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, p);
            q = e2 + p;
            e2 = DoublePrecision.twoSumLow(e2, p, q);
            double e4 = e3 + q;
            e3 = DoublePrecision.twoSumLow(e3, q, e4);


            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            p = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, p);
            q = e3 + p;
            e3 = DoublePrecision.twoSumLow(e3, p, q);
            double e5 = e4 + q;
            e4 = DoublePrecision.twoSumLow(e4, q, e5);


            q = e2 + f2;
            e2 = DoublePrecision.twoSumLow(e2, f2, q);
            p = e3 + q;
            e3 = DoublePrecision.twoSumLow(e3, q, p);
            q = e4 + p;
            e4 = DoublePrecision.twoSumLow(e4, p, q);
            double e6 = e5 + q;
            e5 = DoublePrecision.twoSumLow(e5, q, e6);


            q = e3 + f3;
            e3 = DoublePrecision.twoSumLow(e3, f3, q);
            p = e4 + q;
            e4 = DoublePrecision.twoSumLow(e4, q, p);
            q = e5 + p;
            e5 = DoublePrecision.twoSumLow(e5, p, q);
            final double e7 = e6 + q;
            e6 = DoublePrecision.twoSumLow(e6, q, e7);


            return getSum(s, summation.sum(e0, e1, e2, e3, e4, e5, e6, e7));
        }












        private static int sumProduct(double a1, double b1, double a2, double b2, double[] e) {

            double e1 = a1 * b1;
            double e0 = DoublePrecision.productLow(a1, b1, e1);


            final double f1 = a2 * b2;
            final double f0 = DoublePrecision.productLow(a2, b2, f1);



            double q = e0 + f0;
            e0 = DoublePrecision.twoSumLow(e0, f0, q);
            double e2 = e1 + q;
            e1 = DoublePrecision.twoSumLow(e1, q, e2);

            q = e1 + f1;
            e1 = DoublePrecision.twoSumLow(e1, f1, q);
            final double e3 = e2 + q;
            e2 = DoublePrecision.twoSumLow(e2, q, e3);


            int ei = 0;
            if (e0 != 0) {
                e[ei++] = e0;
            }
            if (e1 != 0) {
                e[ei++] = e1;
            }
            if (e2 != 0) {
                e[ei++] = e2;
            }


            e[ei++] = e3;
            return ei;
        }












        private static void growExpansion(double[] expansion, int length, int start, double value) {
            double p = value;
            for (int i = start; i < length; i++) {
                final double ei = expansion[i];
                final double q = ei + p;
                expansion[i] = DoublePrecision.twoSumLow(ei, p, q);

                p = q;
            }
            expansion[length] = p;
        }









        private static int zeroElimination(double[] e, int size) {
            int newSize = 0;

            while (newSize < size && e[newSize] != 0) {
                newSize++;
            }
            if (newSize != size) {





                for (int i = newSize + 1; i < size; i++) {
                    if (e[i] != 0) {
                        e[newSize++] = e[i];
                    }
                }
            }
            return newSize;
        }
    }
















    static double getSum(double sum, double hpSum) {
        if (!Double.isFinite(hpSum)) {



            return sum;
        }
        return hpSum;
    }
}
