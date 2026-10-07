















package org.apache.commons.numbers.core;

import org.apache.commons.rng.UniformRandomProvider;



final class DoubleTestUtils {


    private DoubleTestUtils() {}






    public static int computeUlpDifference(final double a, final double b) {
        return (int) (Double.doubleToLongBits(a) - Double.doubleToLongBits(b));
    }









    public static double[] randomArray(final int len, final int minExp, final int maxExp,
            final UniformRandomProvider rng) {
        final double[] v = new double[len];
        for (int i = 0; i < v.length; ++i) {
            v[i] = randomDouble(minExp, maxExp, rng);
        }
        return v;
    }







    public static double randomDouble(final int minExp, final int maxExp, final UniformRandomProvider rng) {

        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;

        final int expRange = maxExp - minExp + 1;
        final long exp = rng.nextInt(expRange) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }






    public static double randomDouble(final UniformRandomProvider rng) {

        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;

        return Double.longBitsToDouble(bits | (1023L << 52));
    }
}
