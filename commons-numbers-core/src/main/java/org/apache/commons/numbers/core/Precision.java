
















package org.apache.commons.numbers.core;

import java.math.BigDecimal;
import java.math.RoundingMode;




public final class Precision {













    public static final double EPSILON;








    public static final double SAFE_MIN = Double.MIN_NORMAL;


    private static final long EXPONENT_OFFSET = 1023L;


    private static final double POSITIVE_ZERO = 0d;

    static {





        EPSILON = Double.longBitsToDouble((EXPONENT_OFFSET - 53L) << 52);
    }




    private Precision() {}

























    public static int compareTo(double x, double y, double eps) {
        if (equals(x, y, eps)) {
            return 0;
        } else if (x < y) {
            return -1;
        } else if (x > y) {
            return 1;
        }

        return Double.compare(x, y);
    }


























    public static int compareTo(final double x, final double y, final int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        } else if (x < y) {
            return -1;
        } else if (x > y) {
            return 1;
        }

        return Double.compare(x, y);
    }









    public static boolean equals(float x, float y) {
        return equals(x, y, 1);
    }









    public static boolean equalsIncludingNaN(float x, float y) {
        final boolean xIsNan = Float.isNaN(x);
        final boolean yIsNan = Float.isNaN(y);

        return (xIsNan | yIsNan) ?
            xIsNan == yIsNan :
            equals(x, y, 1);
    }












    public static boolean equals(float x, float y, float eps) {
        return equals(x, y, 1) || Math.abs(y - x) <= eps;
    }












    public static boolean equalsIncludingNaN(float x, float y, float eps) {
        return equalsIncludingNaN(x, y, 1) || Math.abs(y - x) <= eps;
    }






















    public static boolean equals(final float x, final float y, final int maxUlps) {
        final int xInt = Float.floatToRawIntBits(x);
        final int yInt = Float.floatToRawIntBits(y);

        final boolean isEqual;
        if ((xInt ^ yInt) < 0) {


            final int deltaPlus = xInt & Integer.MAX_VALUE;
            final int deltaMinus = yInt & Integer.MAX_VALUE;


            isEqual = (long) deltaPlus + deltaMinus <= maxUlps;
        } else {

            isEqual = Math.abs(xInt - yInt) <= maxUlps;
        }

        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }












    public static boolean equalsIncludingNaN(float x, float y, int maxUlps) {
        final boolean xIsNan = Float.isNaN(x);
        final boolean yIsNan = Float.isNaN(y);

        return (xIsNan | yIsNan) ?
            xIsNan == yIsNan :
            equals(x, y, maxUlps);
    }









    public static boolean equals(double x, double y) {
        return equals(x, y, 1);
    }









    public static boolean equalsIncludingNaN(double x, double y) {
        final boolean xIsNan = Double.isNaN(x);
        final boolean yIsNan = Double.isNaN(y);

        return (xIsNan | yIsNan) ?
            xIsNan == yIsNan :
            equals(x, y, 1);
    }












    public static boolean equals(double x, double y, double eps) {
        return equals(x, y, 1) || Math.abs(y - x) <= eps;
    }













    public static boolean equalsWithRelativeTolerance(double x, double y, double eps) {
        if (equals(x, y, 1)) {
            return true;
        }

        final double absoluteMax = Math.max(Math.abs(x), Math.abs(y));
        final double relativeDifference = Math.abs((x - y) / absoluteMax);

        return relativeDifference <= eps;
    }












    public static boolean equalsIncludingNaN(double x, double y, double eps) {
        return equalsIncludingNaN(x, y) || Math.abs(y - x) <= eps;
    }






















    public static boolean equals(final double x, final double y, final int maxUlps) {
        final long xInt = Double.doubleToRawLongBits(x);
        final long yInt = Double.doubleToRawLongBits(y);

        if ((xInt ^ yInt) < 0) {


            final long deltaPlus = xInt & Long.MAX_VALUE;
            final long deltaMinus = yInt & Long.MAX_VALUE;







            return deltaPlus <= maxUlps && deltaMinus <= (maxUlps - deltaPlus);
        }


        return Math.abs(xInt - yInt) <= maxUlps && !Double.isNaN(x) && !Double.isNaN(y);
    }












    public static boolean equalsIncludingNaN(double x, double y, int maxUlps) {
        final boolean xIsNan = Double.isNaN(x);
        final boolean yIsNan = Double.isNaN(y);

        return (xIsNan | yIsNan) ?
            xIsNan == yIsNan :
            equals(x, y, maxUlps);
    }














    public static double round(double x, int scale) {
        return round(x, scale, RoundingMode.HALF_UP);
    }


















































    public static double round(double x,
                               int scale,
                               RoundingMode roundingMode) {
        try {
            final double rounded = new BigDecimal(Double.toString(x))
                   .setScale(scale, roundingMode)
                   .doubleValue();

            return rounded == POSITIVE_ZERO ? POSITIVE_ZERO * x : rounded;
        } catch (NumberFormatException ex) {
            if (Double.isInfinite(x)) {
                return x;
            }
            return Double.NaN;
        }
    }












    public static double representableDelta(double x,
                                            double delta) {
        return x + delta - x;
    }








    public static DoubleEquivalence doubleEquivalenceOfEpsilon(final double eps) {
        if (!Double.isFinite(eps) ||
            eps < 0d) {
            throw new IllegalArgumentException("Invalid epsilon value: " + eps);
        }

        return new DoubleEquivalence() {

            private final double epsilon = eps;


            @Override
            public int compare(double a,
                               double b) {
                return compareTo(a, b, epsilon);
            }
        };
    }







    @FunctionalInterface
    public interface DoubleEquivalence {







        default boolean eq(double a, double b) {
            return compare(a, b) == 0;
        }








        default boolean eqZero(double a) {
            return eq(a, 0d);
        }








        default boolean lt(double a, double b) {
            return compare(a, b) < 0;
        }








        default boolean lte(double a, double b) {
            return compare(a, b) <= 0;
        }








        default boolean gt(double a, double b) {
            return compare(a, b) > 0;
        }








        default boolean gte(double a, double b) {
            return compare(a, b) >= 0;
        }


















        default double signum(double a) {
            if (a == 0d || Double.isNaN(a)) {
                return a;
            }
            return eqZero(a) ?
                Math.copySign(0d, a) :
                Math.copySign(1d, a);
        }
















        int compare(double a, double b);
    }
}
