















package org.apache.commons.numbers.core;



















public final class DDExt {

    private static final int LARGE_N = 100000000;



    private static final int SAFE_EXPONENT_F = 958;



    private static final double SAFE_F = 0.9999996907846553;


    private static final int SAFE_EXPONENT_2F = 1013;



    private static final double SAFE_2F = 1.0000003269678954;

    private static final double LN2 = 0.69314718055994530941;

    private static final double ROOT_HALF = 0.707106781186547524400;


    private static final double SAFE_MULTIPLY = 0x1.0p500;



    private static final double SAFE_MULTIPLY_DOWNSCALE = 0x1.0p-500;




    private DDExt() {}













    public static DD add(DD x, double y) {
        return DD.accurateAdd(x.hi(), x.lo(), y);
    }














    public static DD add(DD x, DD y) {
        return DD.accurateAdd(x.hi(), x.lo(), y.hi(), y.lo());
    }











    public static DD subtract(DD x, double y) {
        return DD.accurateAdd(x.hi(), x.lo(), -y);
    }











    public static DD subtract(DD x, DD y) {
        return DD.accurateAdd(x.hi(), x.lo(), -y.hi(), -y.lo());
    }












    public static DD multiply(DD x, double y) {
        return accurateMultiply(x.hi(), x.lo(), y);
    }













    private static DD accurateMultiply(double x, double xx, double y) {


        final double xh = DD.highPart(x);
        final double xl = x - xh;
        final double xxh = DD.highPart(xx);
        final double xxl = xx - xxh;
        final double yh = DD.highPart(y);
        final double yl = y - yh;

        final double p00 = x * y;
        final double q00 = DD.twoProductLow(xh, xl, yh, yl, p00);
        final double p10 = xx * y;
        final double q10 = DD.twoProductLow(xxh, xxl, yh, yl, p10);




        final double s0 = p00;

        final double s1 = p10 + q00;
        final double r2 = DD.twoSumLow(p10, q00, s1);


        final double u = s0 + s1;
        final double v = DD.fastTwoSumLow(s0, s1, u);
        return DD.fastTwoSum(u, r2 + q10 + v);
    }












    public static DD multiply(DD x, DD y) {
        return accurateMultiply(x.hi(), x.lo(), y.hi(), y.lo());
    }














    private static DD accurateMultiply(double x, double xx, double y, double yy) {
















        final double xh = DD.highPart(x);
        final double xl = x - xh;
        final double xxh = DD.highPart(xx);
        final double xxl = xx - xxh;
        final double yh = DD.highPart(y);
        final double yl = y - yh;
        final double yyh = DD.highPart(yy);
        final double yyl = yy - yyh;

        final double p00 = x * y;
        final double q00 = DD.twoProductLow(xh, xl, yh, yl, p00);
        final double p01 = x * yy;
        final double q01 = DD.twoProductLow(xh, xl, yyh, yyl, p01);
        final double p10 = xx * y;
        final double q10 = DD.twoProductLow(xxh, xxl, yh, yl, p10);
        final double p11 = xx * yy;







        final double s0 = p00;

        double u = p01 + p10;
        double v = DD.twoSumLow(p01, p10, u);
        final double s1 = q00 + u;
        final double w = DD.twoSumLow(q00, u, s1);
        final double r2 = v + w;


        u = s0 + s1;
        v = DD.fastTwoSumLow(s0, s1, u);
        return DD.fastTwoSum(u, r2 + p11 + q01 + q10 + v);
    }











    public static DD square(DD x) {
        return accurateSquare(x.hi(), x.lo());
    }












    private static DD accurateSquare(double x, double xx) {


        final double xh = DD.highPart(x);
        final double xl = x - xh;
        final double xxh = DD.highPart(xx);
        final double xxl = xx - xxh;

        final double p00 = x * x;
        final double q00 = DD.twoSquareLow(xh, xl, p00);
        final double p01 = x * xx;
        final double q01 = DD.twoProductLow(xh, xl, xxh, xxl, p01);
        final double p11 = xx * xx;




        final double s0 = p00;

        final double s1 = q00 + 2 * p01;
        final double r2 = DD.twoSumLow(q00, 2 * p01, s1);


        final double u = s0 + s1;
        final double v = DD.fastTwoSumLow(s0, s1, u);
        return DD.fastTwoSum(u, r2 + p11 + 2 * q01 + v);
    }















    public static DD divide(DD x, DD y) {
        return accurateDivide(x.hi(), x.lo(), y.hi(), y.lo());
    }

















    private static DD accurateDivide(double x, double xx, double y, double yy) {


        final double q0 = x / y;

        DD p = accurateMultiply(y, yy, q0);
        DD r = DD.accurateAdd(x, xx, -p.hi(), -p.lo());

        final double q1 = r.hi() / y;

        p = accurateMultiply(y, yy, q1);
        r = DD.accurateAdd(r.hi(), r.lo(), -p.hi(), -p.lo());

        final double q2 = r.hi() / y;

        final DD q = DD.fastTwoSum(q0, q1);
        return DD.twoSum(q.hi(), q.lo() + q2);
    }












    public static DD reciprocal(DD y) {
        return accurateReciprocal(y.hi(), y.lo());
    }













    private static DD accurateReciprocal(double y, double yy) {


        final double q0 = 1 / y;

        DD p = accurateMultiply(y, yy, q0);


        DD r = DD.accurateAdd(-p.hi(), -p.lo(), 1);

        final double q1 = r.hi() / y;

        p = accurateMultiply(y, yy, q1);

        r = DD.accurateAdd(r.hi(), r.lo(), -p.hi(), -p.lo());

        final double q2 = r.hi() / y;

        final DD q = DD.fastTwoSum(q0, q1);
        return DD.twoSum(q.hi(), q.lo() + q2);
    }























    public static DD sqrt(DD x) {

        final DD c = x.sqrt();



        if (DD.isNotNormal(c.hi())) {
            return c;
        }



        final DD u = square(c);
        final double cc = (x.hi() - u.hi() - u.lo() + x.lo()) * 0.5 / c.hi();
        return DD.fastTwoSum(c.hi(), c.lo() + cc);
    }

























    public static DD simplePow(double x, double xx, int n) {

        if (n == 0) {
            return DD.ONE;
        }

        if (!Double.isFinite(x) || x == 0) {
            return DD.of(Math.pow(x, n));
        }

        if (n < 0) {
            DD r = computeSimplePow(x, xx, -1L * n);


            if (Math.abs(r.hi()) < SAFE_MULTIPLY_DOWNSCALE) {
                r = DD.of(r.hi() * SAFE_MULTIPLY, r.lo() * SAFE_MULTIPLY).reciprocal();
                final double hi = r.hi() * SAFE_MULTIPLY;

                final double lo = r.lo() * (Double.isInfinite(hi) ? 0 : SAFE_MULTIPLY);
                return DD.of(hi, lo);
            }
            if (Math.abs(r.hi()) > SAFE_MULTIPLY) {
                r = DD.of(r.hi() * SAFE_MULTIPLY_DOWNSCALE, r.lo() * SAFE_MULTIPLY_DOWNSCALE).reciprocal();
                final double hi = r.hi() * SAFE_MULTIPLY_DOWNSCALE;
                final double lo = r.lo() * SAFE_MULTIPLY_DOWNSCALE;
                return DD.of(hi, lo);
            }
            return r.reciprocal();
        }
        return computeSimplePow(x, xx, n);
    }













    private static DD computeSimplePow(double x, double xx, long n) {
        final double y = Math.pow(x, n);
        final double z = xx / x;




        double w;
        if (n > LARGE_N) {
            w = Math.expm1(n * Math.log1p(z));
        } else {
            w = n * z * (1 + (n - 1) * z * 0.5);
        }





        return DD.fastTwoSum(y, y * w);
    }



































    public static DD simplePowScaled(double x, double xx, int n, long[] exp) {

        if (n == 0) {
            exp[0] = 1;
            return DD.of(0.5);
        }

        if (!Double.isFinite(x) || x == 0) {
            exp[0] = 0;
            return DD.of(Math.pow(x, n));
        }

        final int[] ie = {0};
        DD f = DD.of(x, xx).frexp(ie);
        final long b = ie[0];
        if (n < 0) {
            f = computeSimplePowScaled(b, f.hi(), f.lo(), -1L * n, exp);

            f = f.reciprocal();

            f = f.frexp(ie);
            exp[0] = ie[0] - exp[0];
            return f;
        }
        return computeSimplePowScaled(b, f.hi(), f.lo(), n, exp);
    }















    private static DD computeSimplePowScaled(long bx, double x, double xx, long n, long[] exp) {


        long b = bx;
        double f0 = x;
        double f1 = xx;



















        long m;
        double af = Math.abs(f0);
        if (af < ROOT_HALF) {


            f0 *= 2;
            f1 *= 2;
            af *= 2;
            b -= 1;
            if (n <= SAFE_EXPONENT_2F || af <= SAFE_2F) {
                m = n;
            } else {


                m = Math.max(SAFE_EXPONENT_2F, (long) (SAFE_EXPONENT_2F * LN2 / Math.log(af)));
            }
        } else {

            if (n <= SAFE_EXPONENT_F || af >= SAFE_F) {
                m = n;
            } else {


                m = Math.max(SAFE_EXPONENT_F, (long) (-SAFE_EXPONENT_F * LN2 / Math.log(af)));
            }
        }

        DD f;
        final int[] expi = {0};

        if (n <= m) {
            f = computeSimplePow(f0, f1, n);
            f = f.frexp(expi);
            exp[0] = b * n + expi[0];
            return f;
        }






        final long q = n / m;
        final long r = n % m;


        f = computeSimplePow(f0, f1, m);
        f = f.frexp(expi);
        long qb = expi[0];


        if (q > 1) {

            f = computeSimplePowScaled(qb, f.hi(), f.lo(), q, exp);
            qb = exp[0];
        }


        if (r == 0) {
            f = f.frexp(expi);
            exp[0] = b * n + qb + expi[0];
            return f;
        }
        if (r == 1) {
            f = f.multiply(DD.of(f0, f1));
            f = f.frexp(expi);
            exp[0] = b * n + qb + expi[0];
            return f;
        }

        final DD t = f;
        f = computeSimplePow(f0, f1, r);
        f = f.frexp(expi);
        final long rb = expi[0];

        f = f.multiply(t);

        f = f.frexp(expi);
        exp[0] = b * n + qb + rb + expi[0];
        return f;
    }
}
