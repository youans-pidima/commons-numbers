















package org.apache.commons.numbers.examples.jmh.core;

import org.apache.commons.rng.UniformRandomProvider;



final class DoubleUtils {


    private DoubleUtils() {}







    static double random(final int minExp, final int maxExp, final UniformRandomProvider rng) {

        final long mask = ((1L << 52) - 1) | 1L << 63;
        final long bits = rng.nextLong() & mask;

        final long exp = rng.nextInt(maxExp - minExp + 1) + minExp + 1023;
        return Double.longBitsToDouble(bits | (exp << 52));
    }









    static double[] randomArray(final int len, final int minExp, final int maxExp,
            final UniformRandomProvider rng) {
        final double[] arr = new double[len];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = random(minExp, maxExp, rng);
        }
        return arr;
    }






    static double[] scalarMultiply(final double[] v, final double s) {
        final double[] sv = new double[v.length];
        for (int i = 0; i < v.length; ++i) {
            sv[i] = s * v[i];
        }
        return sv;
    }
}
