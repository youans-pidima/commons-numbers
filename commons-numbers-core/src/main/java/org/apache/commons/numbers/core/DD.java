















package org.apache.commons.numbers.core;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.function.DoubleUnaryOperator;



















































































































































public final class DD
    extends Number
    implements NativeOperators<DD>,
               Serializable {









































    public static final DD ONE = new DD(1, 0);



    public static final DD ZERO = new DD(0, 0);







    private static final double MULTIPLIER = 1.0 + 0x1.0p27;


    private static final int EXP_MASK = 0x7ff;


    private static final int CMP_UNSIGNED_2046 = Integer.MIN_VALUE + 2046;


    private static final int CMP_UNSIGNED_MINUS_1 = Integer.MIN_VALUE - 1;


    private static final int CMP_UNSIGNED_1022 = Integer.MIN_VALUE + 1022;

    private static final double TWO_POW_512 = 0x1.0p512;

    private static final double TWO_POW_M512 = 0x1.0p-512;

    private static final double TWO_POW_53 = 0x1.0p53;

    private static final long HIGH32_MASK = 0xffff_ffff_0000_0000L;

    private static final long UNSIGN_MASK = 0x7fff_ffff_ffff_ffffL;

    private static final long MANTISSA_MASK = 0x000f_ffff_ffff_ffffL;

    private static final int EXPONENT_OFFSET = 1023;

    private static final double HALF = 0.5;


    private static final double SAFE_MULTIPLY = 0x1.0p500;









    private static final int TO_STRING_SIZE = 64;

    private static final char FORMAT_START = '(';

    private static final char FORMAT_END = ')';

    private static final char FORMAT_SEP = ',';


    private static final long serialVersionUID = 20230701L;


    private final double x;

    private final double xx;







    private DD(double x, double xx) {
        this.x = x;
        this.xx = xx;
    }









    public static DD of(double x) {
        return new DD(x, 0);
    }
















    static DD of(double x, double xx) {
        return new DD(x, xx);
    }












    public static DD of(int x) {
        return new DD(x, 0);
    }












    public static DD of(long x) {





        final long a = x & HIGH32_MASK;
        final long b = x - a;


        return fastTwoSum(a, b);
    }

















    public static DD ofUnsigned(int x) {
        return new DD(Integer.toUnsignedLong(x), 0);
    }

























    public static DD ofUnsigned(long x) {

        final long a = x & HIGH32_MASK;
        final long b = x - a;

        final double aa = (a >>> 1) * 2.0;
        return fastTwoSum(aa, b);
    }


















    public static DD from(BigDecimal x) {
        final double z = x.doubleValue();

        final double zz = Double.isInfinite(z) ? 0 : x.subtract(new BigDecimal(z)).doubleValue();

        return new DD(z, zz);
    }



























    public static DD ofSum(double x, double y) {
        return twoSum(x, y);
    }


















    public static DD ofDifference(double x, double y) {
        return twoDiff(x, y);
    }




























    public static DD ofProduct(double x, double y) {
        return twoProd(x, y);
    }















    public static DD ofSquare(double x) {
        return twoSquare(x);
    }


























    public static DD fromQuotient(double x, double y) {


        final double q0 = x / y;

        final double p0 = q0 * y;
        final double p1 = twoProductLow(q0, y, p0);
        final double r0 = x - p0;
        final double r1 = twoDiffLow(x, p0, r0) - p1;

        final double q1 = (r0 + r1) / y;
        return new DD(q0, q1);
    }












    public double hi() {
        return x;
    }










    public double lo() {
        return xx;
    }

















    public boolean isFinite() {
        return Double.isFinite(x + xx);
    }















    @Override
    public double doubleValue() {
        return x + xx;
    }


















    @Override
    public float floatValue() {
        return (float) doubleValue();
    }




























    @Override
    public int intValue() {

        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, longValue()));
    }




























    @Override
    public long longValue() {

        final long a = (long) x;




        if (a == x) {


            final long a1;
            final long a2;
            if (Math.abs(x) > TWO_POW_53) {
                a1 = (long) (x * 0.5);
                a2 = a1;
            } else {
                a1 = a;
                a2 = 0;
            }




            final long b = (long) (a < 0 ? Math.ceil(xx) : Math.floor(xx));

            final long sum = a1 + b + a2;



            if ((sum ^ a) >= 0) {
                return sum;
            }
        }


        return a;
    }












    public BigDecimal bigDecimalValue() {
        return new BigDecimal(x).add(new BigDecimal(xx));
    }


















    static DD fastTwoSum(double a, double b) {
        final double x = a + b;
        return new DD(x, fastTwoSumLow(a, b, x));
    }














    static double fastTwoSumLow(double a, double b, double x) {



        return b - (x - a);
    }















    static DD fastTwoDiff(double a, double b) {
        final double x = a - b;
        return new DD(x, fastTwoDiffLow(a, b, x));
    }












    private static double fastTwoDiffLow(double a, double b, double x) {



        return (a - x) - b;
    }













    static DD twoSum(double a, double b) {
        final double x = a + b;
        return new DD(x, twoSumLow(a, b, x));
    }












    static double twoSumLow(double a, double b, double x) {






        final double bVirtual = x - a;
        return (a - (x - bVirtual)) + (b - bVirtual);
    }












    static DD twoDiff(double a, double b) {
        final double x = a - b;
        return new DD(x, twoDiffLow(a, b, x));
    }











    private static double twoDiffLow(double a, double b, double x) {






        final double bVirtual = a - x;
        return (a - (x + bVirtual)) - (b - bVirtual);
    }




























    static DD twoProd(double x, double y) {
        final double xy = x * y;

        return new DD(xy, twoProductLow(x, y, xy));
    }
















    static double twoProductLow(double x, double y, double xy) {

        final double hx = highPart(x);
        final double lx = x - hx;
        final double hy = highPart(y);
        final double ly = y - hy;
        return twoProductLow(hx, lx, hy, ly, xy);
    }














    static double twoProductLow(double hx, double lx, double hy, double ly, double xy) {





        return lx * ly - (((xy - hx * hy) - lx * hy) - hx * ly);
    }















    static DD twoSquare(double x) {
        final double xx = x * x;

        return new DD(xx, twoSquareLow(x, xx));
    }
















    static double twoSquareLow(double x, double x2) {

        final double hx = highPart(x);
        final double lx = x - hx;
        return twoSquareLow(hx, lx, x2);
    }












    static double twoSquareLow(double hx, double lx, double x2) {
        return lx * lx - ((x2 - hx * hx) - 2 * lx * hx);
    }




























    static double highPart(double value) {
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }








    @Override
    public DD negate() {
        return new DD(-x, -xx);
    }

















    public DD abs() {

        if (x < 0) {
            return negate();
        }


        return x == 0 ? ZERO : this;
    }



























    public DD floor() {
        return floorOrCeil(x, xx, Math::floor);
    }



























    public DD ceil() {
        return floorOrCeil(x, xx, Math::ceil);
    }

















    private static DD floorOrCeil(double x, double xx, DoubleUnaryOperator op) {

        final double y = op.applyAsDouble(x);

        if (y == x) {

            if (isNotNormal(y)) {
                return new DD(y, 0);
            }






            final double yy = op.applyAsDouble(xx) + 0;
            return fastTwoSum(y, yy);
        }


        return new DD(y, 0);
    }













    public DD add(double y) {

        final double s0 = x + y;
        final double s1 = twoSumLow(x, y, s0);


        return fastTwoSum(s0, s1 + xx);
    }









    @Override
    public DD add(DD y) {
        return add(x, xx, y.x, y.xx);
    }













    static DD add(double x, double xx, double y, double yy) {


        final double s0 = x + y;
        final double s1 = twoSumLow(x, y, s0);

        final double t0 = xx + yy;
        final double t1 = twoSumLow(xx, yy, t0);



        final DD zz = fastTwoSum(s0, s1 + t0);
        return fastTwoSum(zz.x, zz.xx + t1);
    }
















    static DD accurateAdd(double x, double xx, double y) {

        DD s = twoSum(xx, y);
        double s2 = s.xx;
        s = twoSum(x, s.x);
        final double s0 = s.x;
        final double s1 = s.xx;

        s = fastTwoSum(s1, s2);
        s2 = s.xx;
        s = fastTwoSum(s0, s.x);




        return fastTwoSum(s.x, s2 + s.xx);
    }


















    static DD accurateAdd(double x, double xx, double y, double yy) {

        DD s = twoSum(xx, yy);
        double s3 = s.xx;
        s = twoSum(x, s.x);

        double s0 = s.x;
        s = twoSum(s.xx, y);
        double s2 = s.xx;
        s = twoSum(s0, s.x);

        s0 = s.x;

        s = fastTwoSum(s.xx, s2);
        final double s1 = s.x;
        s = fastTwoSum(s.xx, s3);

        s3 = s.xx;
        s = fastTwoSum(s1, s.x);
        s2 = s.xx;
        s = fastTwoSum(s0, s.x);




        return fastTwoSum(s.x, s3 + s2 + s.xx);
    }












    public DD subtract(double y) {
        return add(-y);
    }











    @Override
    public DD subtract(DD y) {
        return add(x, xx, -y.x, -y.xx);
    }













    public DD multiply(double y) {
        return multiply(x, xx, y);
    }















    private static DD multiply(double x, double xx, double y) {


        final double hi = x * y;
        final double lo = twoProductLow(x, y, hi);


        return fastTwoSum(hi, lo + xx * y);
    }









    @Override
    public DD multiply(DD y) {
        return multiply(x, xx, y.x, y.xx);
    }












    private static DD multiply(double x, double xx, double y, double yy) {


        final double hi = x * y;
        final double lo = twoProductLow(x, y, hi);
        return fastTwoSum(hi, lo + (x * yy + xx * y));
    }











    public DD square() {
        return square(x, xx);
    }








    private static DD square(double x, double xx) {

        final double hi = x * x;
        final double lo = twoSquareLow(x, hi);
        return fastTwoSum(hi, lo + (2 * x * xx));
    }










    public DD divide(double y) {
        return divide(x, xx, y);
    }












    private static DD divide(double x, double xx, double y) {


        final double q0 = x / y;

        DD p = twoProd(y, q0);

        DD r = accurateAdd(x, xx, -p.x, -p.xx);

        final double q1 = r.x / y;

        p = twoProd(y, q1);

        r = add(r.x, r.xx, -p.x, -p.xx);

        final double q2 = r.x / y;

        final DD q = fastTwoSum(q0, q1);
        return twoSum(q.x, q.xx + q2);
    }










    @Override
    public DD divide(DD y) {
        return divide(x, xx, y.x, y.xx);
    }













    private static DD divide(double x, double xx, double y, double yy) {


        final double q0 = x / y;

        DD p = multiply(y, yy, q0);

        DD r = accurateAdd(x, xx, -p.x, -p.xx);

        final double q1 = r.x / y;

        p = multiply(y, yy, q1);

        r = add(r.x, r.xx, -p.x, -p.xx);

        final double q2 = r.x / y;

        final DD q = fastTwoSum(q0, q1);
        return twoSum(q.x, q.xx + q2);
    }









    @Override
    public DD reciprocal() {
        return reciprocal(x, xx);
    }











    private static DD reciprocal(double y, double yy) {


        final double q0 = 1 / y;

        DD p = multiply(y, yy, q0);


        DD r = accurateAdd(-p.x, -p.xx, 1);

        final double q1 = r.x / y;

        p = multiply(y, yy, q1);

        r = add(r.x, r.xx, -p.x, -p.xx);

        final double q2 = r.x / y;

        final DD q = fastTwoSum(q0, q1);
        return twoSum(q.x, q.xx + q2);
    }




















    public DD sqrt() {

        final double c = Math.sqrt(x);




        if (isNotNormal(c)) {
            return new DD(c, 0);
        }





        final double hc = highPart(c);
        final double lc = c - hc;
        final double u = c * c;
        final double uu = twoSquareLow(hc, lc, u);
        final double cc = (x - u - uu + xx) * 0.5 / c;




        return fastTwoSum(c, cc);
    }











    static boolean isNotNormal(double a) {




        final int baisedExponent = (int) (Double.doubleToRawLongBits(a) >>> 52) & EXP_MASK;

        return baisedExponent + CMP_UNSIGNED_MINUS_1 >= CMP_UNSIGNED_2046;
    }
























    public DD scalb(int exp) {



        if (exp + CMP_UNSIGNED_1022 < CMP_UNSIGNED_2046) {
            final double s = twoPow(exp);
            return new DD(x * s, xx * s);
        }






        final int n;
        final int m;
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






        final double z0;
        final double z1;


        if (n >= 5) {



            p *= p * 0.5;
            z0 = x * p * p * p;
            z1 = xx * p * p * p;
            return new DD(z0, z1);
        }

        final double s = twoPow(m);
        if (n == 4) {
            z0 = x * s * p * p * p * p;
            z1 = xx * s * p * p * p * p;
        } else if (n == 3) {
            z0 = x * s * p * p * p;
            z1 = xx * s * p * p * p;
        } else if (n == 2) {
            z0 = x * s * p * p;
            z1 = xx * s * p * p;
        } else {

            z0 = x * s * p;
            z1 = xx * s * p;
        }
        return new DD(z0, z1);
    }









    static double twoPow(int n) {
        return Double.longBitsToDouble((long) (n + 1023) << 52);
    }




























    public DD frexp(int[] exp) {
        exp[0] = getScale(x);

        if (exp[0] == Double.MAX_EXPONENT + 1) {





            exp[0] = 0;
            return this;
        }

        exp[0] += 1;
        DD f = scalb(-exp[0]);



        if (Math.abs(f.x) == HALF && 2 * f.x * f.xx < 0) {
            f = new DD(f.x * 2, f.xx * 2);
            exp[0] -= 1;
        }
        return f;
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




























    @Override
    public DD pow(int n) {

        if (n == 1) {
            return this;
        }
        if (n == 0) {
            return ONE;
        }


        if (isNotNormal(x)) {


            return new DD(Math.pow(x, n), 0);
        }


        if (n == -1) {
            return reciprocal();
        }



        if (n < 0) {

            return computePow(x, xx, -n).reciprocal();
        }
        return computePow(x, xx, n);
    }












    private static DD computePow(double x, double xx, int n) {















        final double xh = highPart(x);
        final double xl = x - xh;


        double f0 = x;
        double f1 = xx;

        double u;
        double v;
        double w;



        final int shift = Integer.numberOfLeadingZeros(n) + 1;
        int bits = n << shift;




        for (int i = 32 - shift; i != 0; i--, bits <<= 1) {


            u = f0 * f0;
            v = twoSquareLow(f0, u);

            w = v + (2 * f0 * f1);
            f0 = u + w;
            f1 = fastTwoSumLow(u, w, f0);
            if (bits < 0) {

                u = highPart(f0);
                v = f0 - u;
                w = f0 * x;
                v = twoProductLow(u, v, xh, xl, w);

                u = v + (f0 * xx + f1 * x);
                f0 = w + u;
                f1 = fastTwoSumLow(w, u, f0);
            }
        }

        return new DD(f0, f1);
    }


































    public DD pow(int n, long[] exp) {

        if (n == 0) {
            exp[0] = 1;
            return new DD(0.5, 0);
        }

        if (!Double.isFinite(x) || x == 0) {
            exp[0] = 0;
            return new DD(Math.pow(x, n), 0);
        }

        final int[] ie = {0};
        DD f = frexp(ie);
        final long b = ie[0];

        if (Math.abs(f.x) == HALF && f.xx == 0) {




            final double y0 = 0.5 * Math.pow(2 * f.x, n);

            final double y1 = Math.copySign(0.0, y0 * f.x * this.xx);
            exp[0] = 1 + (b - 1) * n;
            return new DD(y0, y1);
        }
        if (n < 0) {
            f = computePowScaled(b, f.x, f.xx, -n, exp);

            f = reciprocal(f.x, f.xx);

            f = f.frexp(ie);
            exp[0] = ie[0] - exp[0];
            return f;
        }
        return computePowScaled(b, f.x, f.xx, n, exp);
    }














    private static DD computePowScaled(long b, double x, double xx, int n, long[] exp) {















        final long be = b - 1;
        final double b0 = x * 2;
        final double b1 = xx * 2;

        final double b0h = highPart(b0);
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
            v = twoSquareLow(f0, u);

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

                u = highPart(f0);
                v = f0 - u;
                w = f0 * b0;
                v = twoProductLow(u, v, b0h, b0l, w);

                u = v + (f0 * b1 + f1 * b0);
                f0 = w + u;
                f1 = fastTwoSumLow(w, u, f0);

            }
        }

        final int[] e = {0};
        final DD f = new DD(f0, f1).frexp(e);
        exp[0] = fe + e[0];
        return f;
    }
















































    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DD) {
            final DD c = (DD) other;
            return equals(x, c.x) && equals(xx, c.xx);
        }
        return false;
    }
















    @Override
    public int hashCode() {
        return 31 * (31 + Double.hashCode(x + 0.0)) + Double.hashCode(xx + 0.0);
    }















    private static boolean equals(double x, double y) {
        return Double.doubleToLongBits(x + 0.0) == Double.doubleToLongBits(y + 0.0);
    }

















    @Override
    public String toString() {
        return new StringBuilder(TO_STRING_SIZE)
            .append(FORMAT_START)
            .append(x).append(FORMAT_SEP)
            .append(xx)
            .append(FORMAT_END)
            .toString();
    }








    @Override
    public DD zero() {
        return ZERO;
    }


    @Override
    public boolean isZero() {

        return x == 0.0;
    }








    @Override
    public DD one() {
        return ONE;
    }


    @Override
    public boolean isOne() {
        return x == 1.0 && xx == 0.0;
    }








    @Override
    public DD multiply(int n) {

        return multiply(x, xx, n);
    }
}
