















package org.apache.commons.numbers.core;









public final class DDMath {

    private static final double HALF = 0.5;


    private static final double SAFE_MULTIPLY = 0x1.0p500;





    private static final class MDD {

        private double x;

        private double xx;


        MDD() {}
    }


    private DDMath() {}




































    public static DD pow(DD x, int n, long[] exp) {

        if (n == 0) {
            exp[0] = 1;
            return DD.of(0.5);
        }

        if (!Double.isFinite(x.hi()) || x.hi() == 0) {
            exp[0] = 0;
            return DD.of(Math.pow(x.hi(), n));
        }

        final int[] ie = {0};
        final DD f = x.frexp(ie);
        final long b = ie[0];

        if (Math.abs(f.hi()) == HALF && f.lo() == 0) {




            final double y0 = 0.5 * Math.pow(2 * f.hi(), n);

            final double y1 = Math.copySign(0.0, y0 * f.hi() * x.lo());
            exp[0] = 1 + (b - 1) * n;
            return DD.of(y0, y1);
        }
        return computePowScaled(b, f.hi(), f.lo(), n, exp);
    }















    private static DD computePowScaled(long b, double x, double xx, int n, long[] exp) {
































        final long be = b - 1;
        final double b0 = x * 2;
        final double b1 = xx * 2;

        final double b0h = DD.highPart(b0);
        final double b0l = b0 - b0h;
        final double b1h = DD.highPart(b1);
        final double b1l = b1 - b1h;


        long fe = be;
        double f0 = b0;
        double f1 = b1;
        double f2 = 0;



        final int an = Math.abs(n);
        final int shift = Integer.numberOfLeadingZeros(an) + 1;
        int bits = an << shift;
        DD t;
        final MDD m = new MDD();



        for (int i = 32 - shift; i != 0; i--, bits <<= 1) {

            fe <<= 1;
            double a0h = DD.highPart(f0);
            double a0l = f0 - a0h;
            double a1h = DD.highPart(f1);
            double a1l = f1 - a1h;
            double a2h = DD.highPart(f2);
            double a2l = f2 - a2h;
            double p00 = f0 * f0;
            double q00 = DD.twoSquareLow(a0h, a0l, p00);
            double p01 = f0 * f1;
            double q01 = DD.twoProductLow(a0h, a0l, a1h, a1l, p01);
            final double p02 = f0 * f2;
            final double q02 = DD.twoProductLow(a0h, a0l, a2h, a2l, p02);
            double p11 = f1 * f1;
            double q11 = DD.twoSquareLow(a1h, a1l, p11);
            final double p12 = f1 * f2;
            double s0 = p00;

            double s1 = 2 * p01 + q00;
            double r2 = DD.twoSumLow(2 * p01, q00, s1);

            double s2 = p02 + q01;
            double r3b = DD.twoSumLow(p02, q01, s2);
            double u = p11 + r2;
            double v = DD.twoSumLow(p11, r2, u);
            t = DD.add(2 * s2, 2 * r3b, u, v);
            s2 = t.hi();
            r3b = t.lo();

            double s3 = 2 * (p12 + q02) + q11 + r3b;
            f0 = norm3(s0, s1, s2, s3, m);
            f1 = m.x;
            f2 = m.xx;


            if (Math.abs(f0) > SAFE_MULTIPLY) {


                final int e = Math.getExponent(f0);
                final double s = DD.twoPow(-e);
                fe += e;
                f0 *= s;
                f1 *= s;
                f2 *= s;
            }

            if (bits < 0) {

                fe += be;
                a0h = DD.highPart(f0);
                a0l = f0 - a0h;
                a1h = DD.highPart(f1);
                a1l = f1 - a1h;
                a2h = DD.highPart(f2);
                a2l = f2 - a2h;
                p00 = f0 * b0;
                q00 = DD.twoProductLow(a0h, a0l, b0h, b0l, p00);
                p01 = f0 * b1;
                q01 = DD.twoProductLow(a0h, a0l, b1h, b1l, p01);
                final double p10 = f1 * b0;
                final double q10 = DD.twoProductLow(a1h, a1l, b0h, b0l, p10);
                p11 = f1 * b1;
                q11 = DD.twoProductLow(a1h, a1l, b1h, b1l, p11);
                final double p20 = f2 * b0;
                final double q20 = DD.twoProductLow(a2h, a2l, b0h, b0l, p20);
                final double p21 = f2 * b1;
                s0 = p00;

                u = p01 + p10;
                v = DD.twoSumLow(p01, p10, u);
                s1 = q00 + u;
                final double w = DD.twoSumLow(q00, u, s1);
                r2 = v + w;
                final double r3a = DD.twoSumLow(v, w, r2);

                s2 = p11 + p20;
                r3b = DD.twoSumLow(p11, p20, s2);
                u = q01 + q10;
                v = DD.twoSumLow(q01, q10, u);
                t = DD.add(s2, r3b, u, v);
                s2 = t.hi() + r2;
                r3b = DD.twoSumLow(t.hi(), r2, s2);

                s3 = p21 + q11 + q20 + r3a + r3b;
                f0 = norm3(s0, s1, s2, s3, m);
                f1 = m.x;
                f2 = m.xx;

            }
        }


        final double u = f1 + f2;
        t = DD.fastTwoSum(f0, u);
        final int[] e = {0};


        if (n < 0) {

            final double v = DD.fastTwoSumLow(f1, f2, u);

            t = inverse3(t.hi(), t.lo(), v);

            t = t.frexp(e);
            exp[0] = e[0] - fe;
            return t;
        }

        t = t.frexp(e);
        exp[0] = fe + e[0];
        return t;
    }











    private static double norm3(double s0, double s1, double s2, double s3, MDD s12) {
        double q;

        final double g0 = s0 + s1;
        q = DD.fastTwoSumLow(s0, s1, g0);
        final double g1 = q + s2;
        q = DD.fastTwoSumLow(q, s2, g1);
        final double g2 = q + s3;
        final double g3 = DD.fastTwoSumLow(q, s3, g2);

        q = g1 + g2;
        s12.xx = DD.fastTwoSumLow(g1, g2, q) + g3;
        final double h0 = g0 + q;
        s12.x = DD.fastTwoSumLow(g0, q, h0);
        return h0;
    }














    private static DD inverse3(double y, double yy, double yyy) {

        double r;
        double rr;
        double rrr;
        double t;

        final double q0 = 1 / y;

        final MDD q = new MDD();
        t = multiply3(y, yy, yyy, q0, q);
        r = add3(-t, -q.x, -q.xx, 1, q);
        rr = q.x;
        rrr = q.xx;

        final double q1 = r / y;

        t = multiply3(y, yy, yyy, q1, q);
        r = add3(-t, -q.x, -q.xx, r, rr, rrr, q);
        rr = q.x;
        rrr = q.xx;

        final double q2 = r / y;

        t = multiply3(y, yy, yyy, q2, q);
        r = add3(-t, -q.x, -q.xx, r, rr, rrr, q);

        final double q3 = r / y;

        t = norm3(q0, q1, q2, q3, q);

        return DD.fastTwoSum(t, q.x + q.xx);
    }











    private static double multiply3(double a0, double a1, double a2, double b, MDD s12) {















        final double a0h = DD.highPart(a0);
        final double a0l = a0 - a0h;
        final double a1h = DD.highPart(a1);
        final double a1l = a1 - a1h;
        final double b0h = DD.highPart(b);
        final double b0l = b - b0h;
        final double p00 = a0 * b;
        final double q00 = DD.twoProductLow(a0h, a0l, b0h, b0l, p00);
        final double p10 = a1 * b;
        final double q10 = DD.twoProductLow(a1h, a1l, b0h, b0l, p10);
        final double p20 = a2 * b;

        final double s1 = p10 + q00;
        final double r1 = DD.twoSumLow(p10, q00, s1);

        double u = p20 + q10;
        final double v = DD.twoSumLow(p20, q10, u);
        final double s2 = u + r1;
        u = DD.twoSumLow(u, r1, s2);
        return norm3(p00, s1, s2, v + u, s12);
    }











    private static double add3(double a0, double a1, double a2, double b, MDD s12) {

        double u;
        final double v;
        final double s0 = a0 + b;
        u = DD.twoSumLow(a0, b, s0);
        final double s1 = a1 + u;
        v = DD.twoSumLow(a1, u, s1);
        final double s2 = a2 + v;
        u = DD.twoSumLow(a2, v, s2);
        return norm3(s0, s1, s2, u, s12);
    }















    private static double add3(double a0, double a1, double a2, double b0, double b1, double b2, MDD s12) {

        double u;
        double v;

        final double s0 = a0 + b0;
        final double r1 = DD.twoSumLow(a0, b0, s0);

        u = a1 + b1;
        v = DD.twoSumLow(a1, b1, u);
        final double s1 = r1 + u;
        u = DD.twoSumLow(r1, u, s1);
        final double r2 = v + u;
        final double r3 = DD.twoSumLow(v, u, r2);

        u = a2 + b2;
        v = DD.twoSumLow(a2, b2, u);
        final double s2 = r2 + u;
        u = DD.twoSumLow(r2, u, s2);
        final double s3 = v + u + r3;
        return norm3(s0, s1, s2, s3, s12);
    }
}
