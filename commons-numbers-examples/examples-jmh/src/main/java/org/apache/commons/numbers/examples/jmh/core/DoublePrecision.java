















package org.apache.commons.numbers.examples.jmh.core;








final class DoublePrecision {






























    private static final double MULTIPLIER = 1.34217729E8;




    private static final double SAFE_UPPER = 0x1.0p996;


    private static final double SAFE_LOWER = 0x1.0p-968;



    private static final double DOWN_SCALE = 0x1.0p-30;



    private static final double UP_SCALE = 0x1.0p30;


    private static final double UP_SCALE2 = 0x1.0p60;

    private static final double DOWN_SCALE2 = 0x1.0p-60;

    private static final double SAFE_UPPER_S = 0x1.0p963;


    private static final long ZERO_LOWER_27_BITS = 0xffff_ffff_f800_0000L;



    private static final int EXP_MASK = 0x7ff;



    private static final int CMP_UNSIGNED_2046 = Integer.MIN_VALUE + 2046;



    private static final int CMP_UNSIGNED_MINUS_1 = Integer.MIN_VALUE - 1;




    static final class Quad {



        double hi;

        double lo;

    }


    private DoublePrecision() {

    }















    static void multiplyUnscaled(double x, double y, Quad z) {

        double p;
        p = x * MULTIPLIER;
        final double hx = x - p + p;
        final double lx = x - hx;
        p = y * MULTIPLIER;
        final double hy = y - p + p;
        final double ly = y - hy;
        p = hx * hy;
        final double q = hx * ly + lx * hy;
        z.hi = p + q;
        z.lo = p - z.hi + q + lx * ly;
    }

























    static void multiply(double x, double y, Quad c) {

        final double xy = x * y;
        if (isNotNormal(xy)) {
            c.hi = xy;

            c.lo = xy - xy;
            return;
        }





        long xb = Double.doubleToRawLongBits(x);
        int xe = getBiasedExponent(xb);
        double xs;
        if (xe == 0) {

            xs = x * 0x1.0p54;
            xb = Double.doubleToRawLongBits(xs);
            xe = getBiasedExponent(xb) - 54;
        }
        xs = getNormalisedFraction(xb);

        long yb = Double.doubleToRawLongBits(y);
        int ye = getBiasedExponent(yb);
        double ys;
        if (ye == 0) {

            ys = y * 0x1.0p54;
            yb = Double.doubleToRawLongBits(ys);
            ye = getBiasedExponent(yb) - 54;
        }
        ys = getNormalisedFraction(yb);




        double z = xs * ys;
        double zz = productLowUnscaled(xs, ys, z);




        int scale = xe + ye - 1023;


        if (scale <= 2046 && scale > 0) {

            final double d = Double.longBitsToDouble((long) scale << 52);
            z *= d;
            zz *= d;
        } else {


            scale -= 1023;
            z = Math.scalb(z, scale);
            zz = Math.scalb(zz, scale);
        }



        c.hi = z;
        c.lo = zz;
    }











    static boolean isNotNormal(double a) {




        final int baisedExponent = (int) (Double.doubleToRawLongBits(a) >>> 52) & EXP_MASK;

        return baisedExponent + CMP_UNSIGNED_MINUS_1 >= CMP_UNSIGNED_2046;
    }







    private static int getBiasedExponent(long bits) {
        return (int)(bits >>> 52) & 0x7ff;
    }







    private static double getNormalisedFraction(long bits) {

        return Double.longBitsToDouble((bits & 0x800f_ffff_ffff_ffffL) | 0x3fe0_0000_0000_0000L);
    }



























    static double highPart(double value) {

        if (Math.abs(value) >= SAFE_UPPER) {

            final double hi = highPartUnscaled(value * DOWN_SCALE) * UP_SCALE;
            if (Double.isInfinite(hi)) {











                return highPartSplit(value);
            }
            return hi;
        }

        return highPartUnscaled(value);
    }













    static double highPartUnscaled(double value) {
        final double c = MULTIPLIER * value;
        return c - (c - value);
    }















    static double highPartSplit(double value) {
        return Double.longBitsToDouble(Double.doubleToRawLongBits(value) & ZERO_LOWER_27_BITS);
    }
































    static double productLow(double x, double y, double xy) {




        if (isNotNormal(xy)) {

            return xy - xy;
        }















        final double a = Math.abs(x);
        final double b = Math.abs(y);
        final double ab = Math.abs(xy);
        if (a + b + ab >= SAFE_UPPER) {

            if (a > b) {
                return productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            }
            return productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }





        if (ab <= SAFE_LOWER) {

            return productLowUnscaled(x * UP_SCALE, y * UP_SCALE, xy * UP_SCALE2) * DOWN_SCALE2;
        }


        return productLowUnscaled(x, y, xy);
    }

































    static double productLowS(double x, double y, double xy) {




        if (isNotNormal(xy)) {

            return xy - xy;
        }


















        final double a = Math.abs(x);
        final double b = Math.abs(y);
        if (a + b + Math.abs(xy) >= SAFE_UPPER_S) {

            if (a > b) {
                return productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            }
            return productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }


        return productLowUnscaled(x * UP_SCALE, y * UP_SCALE, xy * UP_SCALE2) * DOWN_SCALE2;
    }
































    static double productLow0(double x, double y, double xy) {




        if (isNotNormal(xy)) {

            return xy - xy;
        }















        final double a = Math.abs(x);
        final double b = Math.abs(y);
        if (a + b + Math.abs(xy) >= SAFE_UPPER) {

            if (a > b) {
                return productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            }
            return productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }


        return productLowUnscaled(x, y, xy);
    }
























    static double productLow1(double x, double y, double xy) {




        final double a = Math.abs(x);
        final double b = Math.abs(y);
        if (a + b + Math.abs(xy) >= SAFE_UPPER) {

            if (a > b) {
                return productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
            }
            return productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }


        return productLowUnscaled(x, y, xy);
    }































    static double productLow2(double x, double y, double xy) {




        if (isNotNormal(xy)) {

            return xy - xy;
        }











        if (Math.abs(x) >= SAFE_UPPER) {
            return productLowUnscaled(x * DOWN_SCALE, y, xy * DOWN_SCALE) * UP_SCALE;
        }
        if (Math.abs(y) >= SAFE_UPPER || Math.abs(xy) >= Double.MAX_VALUE / 4) {
            return productLowUnscaled(x, y * DOWN_SCALE, xy * DOWN_SCALE) * UP_SCALE;
        }

        return productLowUnscaled(x, y, xy);
    }
























    static double productLow3(double x, double y, double xy) {

        final double hx = highPart(x);
        final double lx = x - hx;

        final double hy = highPart(y);
        final double ly = y - hy;

        return productLow(hx, lx, hy, ly, xy);
    }





















    static double productLowSplit(double x, double y, double xy) {

        final double hx = highPartSplit(x);
        final double lx = x - hx;

        final double hy = highPartSplit(y);
        final double ly = y - hy;

        return productLow(hx, lx, hy, ly, xy);
    }

















    static double productLowUnscaled(double x, double y, double xy) {

        final double hx = highPartUnscaled(x);
        final double lx = x - hx;

        final double hy = highPartUnscaled(y);
        final double ly = y - hy;

        return productLow(hx, lx, hy, ly, xy);
    }




















    static double productLow(double hx, double lx, double hy, double ly, double xy) {





        return lx * ly - (((xy - hx * hy) - lx * hy) - hx * ly);
    }




















    static double sumLow(double x, double xx, double y, double yy, double r) {
        return Math.abs(x) > Math.abs(y) ?
                x - r + y + yy + xx :
                y - r + x + xx + yy;
    }













    static double fastTwoSumLow(double a, double b, double sum) {


        return b - (sum - a);
    }













    static double twoSumLow(double a, double b, double sum) {
        final double bVirtual = sum - a;



        return (a - (sum - bVirtual)) + (b - bVirtual);
    }
}
