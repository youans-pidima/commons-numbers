















package org.apache.commons.numbers.gamma;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.DoubleConsumer;
import java.util.function.LongConsumer;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;




final class TestUtils {

    private static final long POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(+0.0);

    private static final long NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);

    private static boolean reportAllDeviations = false;









    static class ErrorStatistics {

        private double ss;

        private double maxAbs;

        private int n;

        private double ps;

        private double psc;

        private double ns;

        private double nsc;




        void add(double x) {


            ss += x * x;
            n++;




            if (x < 0) {
                final double s = nsc + x;
                nsc = twoSumLow(nsc, x, s);
                final double t = ns + s;
                nsc += twoSumLow(ns, s, t);
                ns = t;

                maxAbs = maxAbs < -x ? -x : maxAbs;
            } else {
                final double s = psc + x;
                psc = twoSumLow(psc, x, s);
                final double t = ps + s;
                psc += twoSumLow(ps, s, t);
                ps = t;

                maxAbs = maxAbs < x ? x : maxAbs;
            }
        }






        int size() {
            return n;
        }











        double getMaxAbs() {
            return maxAbs;
        }









        double getRMS() {
            return Math.sqrt(ss / n);
        }









        double getMean() {






            double s;
            double t;
            s = nsc + psc;
            double a = twoSumLow(nsc, psc, s);
            t = s + ps;
            double b = twoSumLow(s, ps, t);
            double c = t;






            s = ns + a;
            a = twoSumLow(ns, a, s);
            t = s + b;
            b = twoSumLow(s, b, t);
            s = t + c;
            c = twoSumLow(t, c, s);
            final double d = s;




            return (a + b + c + d) / n;
        }













        static double twoSumLow(double a, double b, double sum) {
            final double bVirtual = sum - a;



            return (a - (sum - bVirtual)) + (b - bVirtual);
        }
    }


    private TestUtils() {

    }



















    static long assertEquals(double expected, double actual, long maxUlps) {
        return assertEquals(expected, actual, maxUlps, null, (Supplier<String>) null);
    }




















    static long assertEquals(double expected, double actual, long maxUlps, String msg) {
        return assertEquals(expected, actual, maxUlps, null, () -> msg);
    }






















    static long assertEquals(double expected, double actual, long maxUlps,
            LongConsumer error, Supplier<String> msg) {
        final long e = Double.doubleToLongBits(expected);
        final long a = Double.doubleToLongBits(actual);





        int sign;
        long delta;
        boolean equal;
        if (e == a) {

            equal = true;
            sign = 0;
            delta = 0;
        } else if ((a ^ e) < 0L) {


            long d1;
            long d2;
            if (a < e) {
                sign = -1;
                d1 = e - POSITIVE_ZERO_DOUBLE_BITS;
                d2 = a - NEGATIVE_ZERO_DOUBLE_BITS;
            } else {
                sign = 1;
                d1 = a - POSITIVE_ZERO_DOUBLE_BITS;
                d2 = e - NEGATIVE_ZERO_DOUBLE_BITS;
            }

            delta = d1 + d2;
            if (delta < 0) {

                equal = false;
            } else {

                equal = delta <= ((maxUlps < 0) ? (-maxUlps - 1) : maxUlps);
            }
        } else {
            if (a < e) {
                sign = -1;
                delta = e - a;
            } else {
                sign = 1;
                delta = a - e;
            }


            sign = a < 0 ? -sign : sign;


            equal = delta <= ((maxUlps < 0) ? (-maxUlps - 1) : maxUlps);
        }

        Assertions.assertEquals(sign, Double.compare(actual, expected));


        if (maxUlps < 0) {

            if (!equal || reportAllDeviations) {
                System.out.printf("%sexpected <%s> != actual <%s> (ulps=%c%s)%n",
                    prefix(msg), expected, actual, sign < 0 ? '-' : ' ', Long.toUnsignedString(delta));
            }

        } else if (!equal) {
            Assertions.fail(String.format("%sexpected <%s> != actual <%s> (ulps=%c%s)",
                prefix(msg), expected, actual, sign < 0 ? '-' : ' ', Long.toUnsignedString(delta)));
        }


        delta = delta < 0 ? Long.MAX_VALUE : delta;
        delta *= sign;
        if (error != null) {
            error.accept(delta);
        }
        return delta;
    }


























    static double assertEquals(BigDecimal expected, double actual, double maxUlps) {
        return assertEquals(expected, actual, maxUlps, null, (Supplier<String>) null);
    }



























    static double assertEquals(BigDecimal expected, double actual, double maxUlps, String msg) {
        return assertEquals(expected, actual, maxUlps, null, () -> msg);
    }














































    static double assertEquals(BigDecimal expected, double actual, double maxUlps,
            DoubleConsumer error, Supplier<String> msg) {
        final double e = expected.doubleValue();

        double delta;
        boolean equal;
        if (e == actual) {

            equal = true;
            delta = 0;
        } else if (!Double.isFinite(e) || !Double.isFinite(actual)) {

            equal = false;
            delta = Double.compare(actual, e) * Double.POSITIVE_INFINITY;
        } else {

            delta = new BigDecimal(actual).subtract(expected)
                        .divide(new BigDecimal(Math.ulp(e)), MathContext.DECIMAL64).doubleValue();

            equal = Math.abs(delta) <= Math.abs(maxUlps);
        }

        if (error != null) {
            error.accept(delta);
        }


        if (maxUlps < 0) {

            if (!equal || reportAllDeviations) {
                System.out.printf("%sexpected <%s> != actual <%s> (ulps=%s)%n",
                    prefix(msg), expected, actual, delta);
            }

        } else if (!equal) {
            Assertions.fail(String.format("%sexpected <%s> != actual <%s> (ulps=%s)",
                prefix(msg), expected, actual, delta));
        }

        return delta;
    }







    private static String prefix(Supplier<String> msg) {
        return msg == null ? "" : msg.get() + ": ";
    }
}
