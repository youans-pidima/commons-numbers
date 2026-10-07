















package org.apache.commons.numbers.examples.jmh.core;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.commons.rng.UniformRandomProvider;




final class LinearCombinationUtils {

    private static final double LN_2 = Math.log(2);


    private LinearCombinationUtils() {}















    static double genDot(double c, UniformRandomProvider rng,
            double[] x, double[] y, double[] computeC) {
        return genDot(c, rng, x, y, computeC, MathContext.UNLIMITED);
    }















































    static double genDot(double c, UniformRandomProvider rng,
            double[] x, double[] y, double[] computeC, MathContext context) {

        if (x.length < 6) {
            throw new IllegalArgumentException("Incorrect usage: n < 6: " + x.length);
        }
        final int n = x.length;
        final int n2 = n / 2;


        final double b = Math.log(c) / LN_2;
        final double b2 = b / 2;

        int[] e = new int[n2];

        e[0] = (int) Math.round(b2) + 1;
        for (int i = 1; i < n2 - 1; i++) {
            e[i] = (int) Math.round(rng.nextDouble() * b2);
        }




        BigDecimal exact = BigDecimal.ZERO;
        for (int i = 0; i < n2; i++) {
            x[i] = Math.scalb(m1p1(rng), e[i]);
            y[i] = Math.scalb(m1p1(rng), e[i]);
            exact = exact.add(new BigDecimal(x[i]).multiply(new BigDecimal(y[i])), context);
        }




        e = new int[n - n2];


        for (int i = 0; i < e.length - 1; i++) {
            e[i] = (int) Math.round(b2 * (e.length - i - 1) / (e.length - 1));
        }

        for (int i = n2; i < x.length; i++) {

            x[i] = Math.scalb(m1p1(rng), e[i - n2]);





            y[i] = (Math.scalb(m1p1(rng), e[i - n2]) - exact.doubleValue()) / x[i];

            exact = exact.add(new BigDecimal(x[i]).multiply(new BigDecimal(y[i])), context);
        }


        for (int i = n; i > 1; i--) {
            final int j = rng.nextInt(i);
            swap(x, i - 1, j);
            swap(y, i - 1, j);
        }














        final double d = exact.doubleValue();
        if (computeC != null) {


            double s1 = 0;
            double s2 = 0;
            for (int i = 0; i < n; i++) {
                s1 += x[i] * x[i];
                s2 += y[i] * y[i];
            }
            computeC[0] = Math.sqrt(s1) * Math.sqrt(s2) / Math.abs(d);
        }
        return d;
    }







    private static double m1p1(UniformRandomProvider rng) {


        return rng.nextDouble() - rng.nextInt(1);
    }








    private static void swap(double[] array, int i, int j) {
        final double tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}
