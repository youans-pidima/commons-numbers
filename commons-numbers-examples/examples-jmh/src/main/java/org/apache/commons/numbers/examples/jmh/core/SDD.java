















package org.apache.commons.numbers.examples.jmh.core;










































final class SDD {








































    private static final double MULTIPLIER = 1.0 + 0x1.0p27;






    private static final double SAFE_UPPER = 0x1.0p996;


    private static final double DOWN_SCALE = 0x1.0p-30;


    private static final double UP_SCALE = 0x1.0p30;


    private static final int EXP_MASK = 0x7ff;


    private static final int CMP_UNSIGNED_2046 = Integer.MIN_VALUE + 2046;


    private static final int CMP_UNSIGNED_MINUS_1 = Integer.MIN_VALUE - 1;


    private static final int CMP_UNSIGNED_1022 = Integer.MIN_VALUE + 1022;

    private static final double TWO_POW_512 = 0x1.0p512;

    private static final double TWO_POW_M512 = 0x1.0p-512;

    private static final long UNSIGN_MASK = 0x7fff_ffff_ffff_ffffL;

    private static final long MANTISSA_MASK = 0x000f_ffff_ffff_ffffL;

    private static final int EXPONENT_OFFSET = 1023;

    private static final double HALF = 0.5;


    private static final double SAFE_MULTIPLY = 0x1.0p500;

    private static final String NOT_NOMALIZED = "Input is not a normalized double-double";


    private double hi;

    private double lo;




    private SDD() {

    }






    private SDD(SDD source) {
        hi = source.hi;
        lo = source.lo;
    }






    SDD copy() {
        return new SDD(this);
    }






    static SDD create() {
        return new SDD();
    }







    static SDD create(double x) {
        final SDD z = new SDD();
        z.hi = x;
        return z;
    }











    static SDD create(double a, double b) {
        return twoSum(a, b, new SDD());
    }






    double hi() {
        return hi;
    }






    double lo() {
        return lo;
    }






    double doubleValue() {
        return hi + lo;
    }








    private SDD set(double x, double xx) {
        hi = x;
        lo = xx;
        return this;
    }








    static SDD set(double x, SDD z) {
        z.set(x, 0);
        return z;
    }










    static SDD set(double x, double xx, SDD z) {
        assert x == x + xx : NOT_NOMALIZED;
        z.set(x, xx);
        return z;
    }
















    static SDD fastTwoSum(double a, double b, SDD s) {



        final double x = a + b;
        s.lo = b - (x - a);
        s.hi = x;
        return s;
    }














    private static double fastTwoSumLow(double a, double b, double x) {
        return b - (x - a);
    }
















    static SDD fastTwoDiff(double a, double b, SDD d) {



        final double x = a - b;
        d.lo = (a - x) - b;
        d.hi = x;
        return d;
    }














    static SDD twoSum(double a, double b, SDD s) {






        final double x = a + b;
        final double bVirtual = x - a;
        s.lo = (a - (x - bVirtual)) + (b - bVirtual);
        s.hi = x;
        return s;
    }












    private static double twoSumLow(double a, double b, double x) {
        final double bVirtual = x - a;
        return (a - (x - bVirtual)) + (b - bVirtual);
    }













    static SDD twoDiff(double a, double b, SDD d) {






        final double x = a - b;
        final double bVirtual = a - x;
        d.lo = (a - (x + bVirtual)) - (b - bVirtual);
        d.hi = x;
        return d;
    }


























    static SDD twoProd(double x, double y, SDD z) {
        final double xy = x * y;
        z.hi = xy;


        if (isNotNormal(xy)) {

            z.lo = xy - xy;
            return z;
        }















        final double a = Math.abs(x);
        final double b = Math.abs(y);
        if (a + b + Math.abs(xy) >= SAFE_UPPER) {

            if (a > b) {
                z.lo = productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            } else {
                z.lo = productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
            }
        } else {

            z.lo = productLowUnscaled(x, y, xy);
        }
        return z;
    }
















    private static double productLowUnscaled(double x, double y, double xy) {

        final double hx = highPartUnscaled(x);
        final double lx = x - hx;

        final double hy = highPartUnscaled(y);
        final double ly = y - hy;






        return lx * ly - (((xy - hx * hy) - lx * hy) - hx * ly);
    }















    private static double productLowUnscaled(double hx, double lx, double hy, double ly, double xy) {
        return lx * ly - (((xy - hx * hy) - lx * hy) - hx * ly);
    }
















    private static double squareLowUnscaled(double x, double x2) {

        final double hx = highPartUnscaled(x);
        final double lx = x - hx;
        return lx * lx - ((x2 - hx * hx) - 2 * lx * hx);
    }













    private static double squareLowUnscaled(double hx, double lx, double x2) {
        return lx * lx - ((x2 - hx * hx) - 2 * lx * hx);
    }




























    private static double highPartUnscaled(double value) {
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }











    private static boolean isNotNormal(double a) {




        final int baisedExponent = (int) (Double.doubleToRawLongBits(a) >>> 52) & EXP_MASK;

        return baisedExponent + CMP_UNSIGNED_MINUS_1 >= CMP_UNSIGNED_2046;
    }














    static SDD fastAdd(double x, double xx, double y, SDD s) {

        twoSum(x, y, s);


        return fastTwoSum(s.hi, s.lo + xx, s);
    }














    static SDD add(double x, double xx, double y, SDD s) {

        twoSum(xx, y, s);
        double s2 = s.lo;
        twoSum(x, s.hi, s);
        final double s1 = s.lo;
        final double s0 = s.hi;

        fastTwoSum(s1, s2, s);
        s2 = s.lo;
        fastTwoSum(s0, s.hi, s);




        return fastTwoSum(s.hi, s2 + s.lo, s);
    }














    static SDD fastAdd(double x, double xx, double y, double yy, SDD s) {


        twoSum(x, y, s);
        final double p = s.hi;
        final double pp = s.lo;

        twoSum(xx, yy, s);
        final double q = s.hi;
        final double qq = s.lo;



        fastTwoSum(p, pp + q, s);
        return fastTwoSum(s.hi, s.lo + qq, s);
    }
















    static SDD add(double x, double xx, double y, double yy, SDD s) {

        twoSum(xx, yy, s);
        double s3 = s.lo;
        twoSum(x, s.hi, s);

        double s0 = s.hi;
        twoSum(s.lo, y, s);
        double s2 = s.lo;
        twoSum(s0, s.hi, s);

        s0 = s.hi;

        fastTwoSum(s.lo, s2, s);
        final double s1 = s.hi;
        fastTwoSum(s.lo, s3, s);

        s3 = s.lo;
        fastTwoSum(s1, s.hi, s);
        s2 = s.lo;
        fastTwoSum(s0, s.hi, s);




        return fastTwoSum(s.hi, s3 + s2 + s.lo, s);
    }














    static SDD multiply(double x, double xx, double y, SDD p) {


        twoProd(x, y, p);


        return fastTwoSum(p.hi, p.lo + xx * y, p);
    }













    static SDD multiply(double x, double xx, double y, double yy, SDD p) {


        twoProd(x, y, p);
        return fastTwoSum(p.hi, p.lo + (x * yy + xx * y), p);
    }





















    static SDD uncheckedMultiply(double x, double xx, double y, double yy, SDD p) {

        final double hi = x * y;
        final double lo = productLowUnscaled(x, y, hi);
        return fastTwoSum(hi, lo + (x * yy + xx * y), p);
    }










    static SDD divide(double x, double y, SDD q) {


        final double q0 = x / y;

        twoProd(q0, y, q);
        final double p1 = q.lo;
        twoDiff(x, q.hi, q);
        q.lo -= p1;

        final double q1 = q.doubleValue() / y;
        return fastTwoSum(q0, q1, q);
    }














    static SDD uncheckedDivide(double x, double y, SDD q) {


        final double q0 = x / y;

        final double p0 = q0 * y;
        final double p1 = productLowUnscaled(q0, y, p0);
        twoDiff(x, p0, q);
        q.lo -= p1;

        final double q1 = q.doubleValue() / y;
        return fastTwoSum(q0, q1, q);
    }












    static SDD divide(double x, double xx, double y, double yy, SDD q) {


        final double q0 = x / y;

        multiply(y, yy, q0, q);
        add(-q.hi, -q.lo, x, xx, q);
        final double r = q.hi;
        final double rr = q.lo;

        final double q1 = r / y;

        multiply(y, yy, q1, q);
        add(-q.hi, -q.lo, r, rr, q);

        final double q2 = q.hi / y;

        fastTwoSum(q0, q1, q);
        return twoSum(q.hi, q.lo + q2, q);
    }










    static SDD inverse(double y, double yy, SDD q) {


        final double q0 = 1 / y;

        multiply(y, yy, q0, q);

        add(-q.hi, -q.lo, 1, q);
        final double r = q.hi;
        final double rr = q.lo;

        final double q1 = r / y;

        multiply(y, yy, q1, q);
        add(-q.hi, -q.lo, r, rr, q);

        final double q2 = q.hi / y;

        fastTwoSum(q0, q1, q);
        return twoSum(q.hi, q.lo + q2, q);
    }

























    static SDD ldexp(double x, double xx, int exp, SDD r) {



        if (exp + CMP_UNSIGNED_1022 < CMP_UNSIGNED_2046) {
            final double s = twoPow(exp);
            r.hi = x * s;
            r.lo = xx * s;
            return r;
        }






        int n;
        int m;
        double p;
        if (exp < 0) {


            n = -exp >>> 9;

            m = -(-exp & 511);
            p = TWO_POW_M512;
        } else {

            n = exp >>> 9;
            m = exp & 511;
            p = TWO_POW_512;
        }







        if (n >= 5) {



            p *= p * 0.5;
            r.hi = x * p * p * p;
            r.lo = xx * p * p * p;
            return r;
        }

        final double s = twoPow(m);
        if (n == 4) {
            r.hi = x * s * p * p * p * p;
            r.lo = xx * s * p * p * p * p;
        } else if (n == 3) {
            r.hi = x * s * p * p * p;
            r.lo = xx * s * p * p * p;
        } else if (n == 2) {
            r.hi = x * s * p * p;
            r.lo = xx * s * p * p;
        } else {

            r.hi = x * s * p;
            r.lo = xx * s * p;
        }
        return r;
    }









    private static double twoPow(int n) {
        return Double.longBitsToDouble((long) (n + 1023) << 52);
    }

































    static int frexp(double x, double xx, SDD f) {
        int exp = getScale(x);

        if (exp == Double.MAX_EXPONENT + 1) {

            f.hi = x;



            f.lo = xx;

            return 0;
        }

        exp += 1;
        ldexp(x, xx, -exp, f);



        if (Math.abs(f.hi) == HALF && 2 * f.hi * f.lo < 0) {
            f.hi *= 2;
            f.lo *= 2;
            exp -= 1;
        }
        return exp;
    }





























    private static int getScale(double a) {

        final long bits = Double.doubleToRawLongBits(a) & UNSIGN_MASK;

        int exp = (int) (bits >>> 52) - EXPONENT_OFFSET;



        if (exp == Double.MIN_EXPONENT - 1) {

            if (bits == 0) {
                return Double.MAX_EXPONENT + 1;
            }




            final long mantissa = bits & MANTISSA_MASK;
            exp -= Long.numberOfLeadingZeros(mantissa << 12);
        }
        return exp;
    }







































    static long fastPowScaled(double x, double xx, int n, SDD f) {

        if (n == 0) {
            f.set(0.5, 0);
            return 1;
        }

        if (!Double.isFinite(x) || x == 0) {
            f.set(Math.pow(x, n), 0);
            return 0;
        }

        assert x == x + xx : NOT_NOMALIZED;
        long b = frexp(x, xx, f);

        if (Math.abs(f.hi) == HALF && f.lo == 0) {




            f.hi = 0.5 * Math.pow(2 * f.hi, n);

            f.lo = Math.copySign(0.0, x * f.hi * xx);
            return 1 + (b - 1) * n;
        }
        if (n < 0) {
            b = computeFastPowScaled(b, f.hi, f.lo, -n, f);

            inverse(f.hi, f.lo, f);

            return -b + frexp(f.hi, f.lo, f);
        }
        return computeFastPowScaled(b, f.hi, f.lo, n, f);
    }














    private static long computeFastPowScaled(long exp, double x, double xx, int n, SDD f) {















        final long be = exp - 1;
        final double b0 = x * 2;
        final double b1 = xx * 2;

        final double b0h = highPartUnscaled(b0);
        final double b0l = b0 - b0h;


        long fe = be;
        double f0 = b0;
        double f1 = b1;

        double u;
        double v;
        double w;



        final int shift = Integer.numberOfLeadingZeros(n) + 1;
        int bits = n << shift;




        for (int i = 32 - shift; i != 0; i--, bits <<= 1) {


            fe <<= 1;
            u = f0 * f0;
            v = squareLowUnscaled(f0, u);

            w = v + (2 * f0 * f1);
            f0 = u + w;
            f1 = fastTwoSumLow(u, w, f0);

            if (Math.abs(f0) > SAFE_MULTIPLY) {


                final int e = Math.getExponent(f0);
                final double s = twoPow(-e);
                fe += e;
                f0 *= s;
                f1 *= s;
            }
            if (bits < 0) {

                fe += be;

                u = highPartUnscaled(f0);
                v = f0 - u;
                w = f0 * b0;
                v = productLowUnscaled(u, v, b0h, b0l, w);

                u = v + (f0 * b1 + f1 * b0);
                f0 = w + u;
                f1 = fastTwoSumLow(w, u, f0);

            }
        }

        return fe + frexp(f0, f1, f);
    }






































    static long powScaled(double x, double xx, int n, SDD f) {

        if (n == 0) {
            f.set(0.5, 0);
            return 1;
        }

        if (!Double.isFinite(x) || x == 0) {
            f.set(Math.pow(x, n), 0);
            return 0;
        }

        assert x == x + xx : NOT_NOMALIZED;
        final long b = frexp(x, xx, f);

        if (Math.abs(f.hi) == HALF && f.lo == 0) {




            f.hi = 0.5 * Math.pow(2 * f.hi, n);

            f.lo = Math.copySign(0.0, x * f.hi * xx);
            return 1 + (b - 1) * n;
        }
        return computePowScaled(b, f.hi, f.lo, n, f);
    }















    private static long computePowScaled(long exp, double x, double xx, int n, SDD f) {
































        final long be = exp - 1;
        final double b0 = x * 2;
        final double b1 = xx * 2;

        final double b0h = highPartUnscaled(b0);
        final double b0l = b0 - b0h;
        final double b1h = highPartUnscaled(b1);
        final double b1l = b1 - b1h;


        long fe = be;
        double f0 = b0;
        double f1 = b1;
        double f2 = 0;



        final int an = Math.abs(n);
        final int shift = Integer.numberOfLeadingZeros(an) + 1;
        int bits = an << shift;



        for (int i = 32 - shift; i != 0; i--, bits <<= 1) {

            fe <<= 1;
            double a0h = highPartUnscaled(f0);
            double a0l = f0 - a0h;
            double a1h = highPartUnscaled(f1);
            double a1l = f1 - a1h;
            double a2h = highPartUnscaled(f2);
            double a2l = f2 - a2h;
            double p00 = f0 * f0;
            double q00 = squareLowUnscaled(a0h, a0l, p00);
            double p01 = f0 * f1;
            double q01 = productLowUnscaled(a0h, a0l, a1h, a1l, p01);
            final double p02 = f0 * f2;
            final double q02 = productLowUnscaled(a0h, a0l, a2h, a2l, p02);
            double p11 = f1 * f1;
            double q11 = squareLowUnscaled(a1h, a1l, p11);
            final double p12 = f1 * f2;
            double s0 = p00;

            double s1 = 2 * p01 + q00;
            double r2 = twoSumLow(2 * p01, q00, s1);

            double s2 = p02 + q01;
            double r3b = twoSumLow(p02, q01, s2);
            double u = p11 + r2;
            double v = twoSumLow(p11, r2, u);
            fastAdd(2 * s2, 2 * r3b, u, v, f);
            s2 = f.hi;
            r3b = f.lo;

            double s3 = 2 * (p12 + q02) + q11 + r3b;
            f0 = norm3(s0, s1, s2, s3, f);
            f1 = f.hi;
            f2 = f.lo;


            if (Math.abs(f0) > SAFE_MULTIPLY) {


                final int e = Math.getExponent(f0);
                final double s = twoPow(-e);
                fe += e;
                f0 *= s;
                f1 *= s;
                f2 *= s;
            }

            if (bits < 0) {

                fe += be;
                a0h = highPartUnscaled(f0);
                a0l = f0 - a0h;
                a1h = highPartUnscaled(f1);
                a1l = f1 - a1h;
                a2h = highPartUnscaled(f2);
                a2l = f2 - a2h;
                p00 = f0 * b0;
                q00 = productLowUnscaled(a0h, a0l, b0h, b0l, p00);
                p01 = f0 * b1;
                q01 = productLowUnscaled(a0h, a0l, b1h, b1l, p01);
                final double p10 = f1 * b0;
                final double q10 = productLowUnscaled(a1h, a1l, b0h, b0l, p10);
                p11 = f1 * b1;
                q11 = productLowUnscaled(a1h, a1l, b1h, b1l, p11);
                final double p20 = f2 * b0;
                final double q20 = productLowUnscaled(a2h, a2l, b0h, b0l, p20);
                final double p21 = f2 * b1;
                s0 = p00;

                u = p01 + p10;
                v = twoSumLow(p01, p10, u);
                s1 = q00 + u;
                final double w = twoSumLow(q00, u, s1);
                r2 = v + w;
                final double r3a = twoSumLow(v, w, r2);

                s2 = p11 + p20;
                r3b = twoSumLow(p11, p20, s2);
                u = q01 + q10;
                v = twoSumLow(q01, q10, u);
                fastAdd(s2, r3b, u, v, f);
                s2 = f.hi + r2;
                r3b = twoSumLow(f.hi, r2, s2);

                s3 = p21 + q11 + q20 + r3a + r3b;
                f0 = norm3(s0, s1, s2, s3, f);
                f1 = f.hi;
                f2 = f.lo;

            }
        }


        final double u = f1 + f2;
        SDD.fastTwoSum(f0, u, f);


        if (n < 0) {

            final double v = fastTwoSumLow(f1, f2, u);

            inverse3(f.hi, f.lo, v, f);

            return -fe + frexp(f.hi, f.lo, f);
        }

        return fe + frexp(f.hi, f.lo, f);
    }











    private static double norm3(double s0, double s1, double s2, double s3, SDD s12) {
        double q;

        final double g0 = s0 + s1;
        q = fastTwoSumLow(s0, s1, g0);
        final double g1 = q + s2;
        q = fastTwoSumLow(q, s2, g1);
        final double g2 = q + s3;
        final double g3 = fastTwoSumLow(q, s3, g2);

        q = g1 + g2;
        s12.lo = fastTwoSumLow(g1, g2, q) + g3;
        final double h0 = g0 + q;
        s12.hi = fastTwoSumLow(g0, q, h0);
        return h0;
    }














    private static SDD inverse3(double y, double yy, double yyy, SDD q) {

        double r;
        double rr;
        double rrr;
        double t;

        final double q0 = 1 / y;

        t = multiply3(y, yy, yyy, q0, q);
        r = add3(-t, -q.hi, -q.lo, 1, q);
        rr = q.hi;
        rrr = q.lo;

        final double q1 = r / y;

        t = multiply3(y, yy, yyy, q1, q);
        r = add3(-t, -q.hi, -q.lo, r, rr, rrr, q);
        rr = q.hi;
        rrr = q.lo;

        final double q2 = r / y;

        t = multiply3(y, yy, yyy, q2, q);
        r = add3(-t, -q.hi, -q.lo, r, rr, rrr, q);

        final double q3 = r / y;

        t = norm3(q0, q1, q2, q3, q);

        return fastTwoSum(t, q.hi + q.lo, q);
    }











    private static double multiply3(double a0, double a1, double a2, double b, SDD s12) {















        final double a0h = highPartUnscaled(a0);
        final double a0l = a0 - a0h;
        final double a1h = highPartUnscaled(a1);
        final double a1l = a1 - a1h;
        final double b0h = highPartUnscaled(b);
        final double b0l = b - b0h;
        final double p00 = a0 * b;
        final double q00 = productLowUnscaled(a0h, a0l, b0h, b0l, p00);
        final double p10 = a1 * b;
        final double q10 = productLowUnscaled(a1h, a1l, b0h, b0l, p10);
        final double p20 = a2 * b;

        final double s1 = p10 + q00;
        final double r1 = twoSumLow(p10, q00, s1);

        double u = p20 + q10;
        final double v = twoSumLow(p20, q10, u);
        final double s2 = u + r1;
        u = twoSumLow(u, r1, s2);
        return norm3(p00, s1, s2, v + u, s12);
    }











    private static double add3(double a0, double a1, double a2, double b, SDD s12) {

        double u;
        double v;
        final double s0 = a0 + b;
        u = twoSumLow(a0, b, s0);
        final double s1 = a1 + u;
        v = twoSumLow(a1, u, s1);
        final double s2 = a2 + v;
        u = twoSumLow(a2, v, s2);
        return norm3(s0, s1, s2, u, s12);
    }















    private static double add3(double a0, double a1, double a2, double b0, double b1, double b2, SDD s12) {

        double u;
        double v;

        final double s0 = a0 + b0;
        final double r1 = twoSumLow(a0, b0, s0);

        u = a1 + b1;
        v = twoSumLow(a1, b1, u);
        final double s1 = r1 + u;
        u = twoSumLow(r1, u, s1);
        final double r2 = v + u;
        final double r3 = twoSumLow(v, u, r2);

        u = a2 + b2;
        v = twoSumLow(a2, b2, u);
        final double s2 = r2 + u;
        u = twoSumLow(r2, u, s2);
        final double s3 = v + u + r3;
        return norm3(s0, s1, s2, s3, s12);
    }
}
