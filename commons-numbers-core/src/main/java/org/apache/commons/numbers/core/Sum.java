















package org.apache.commons.numbers.core;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
































































public final class Sum
    implements DoubleSupplier,
               DoubleConsumer {

    private double sum;

    private double comp;






    private Sum(final double initialValue) {
        sum = initialValue;
    }







    public Sum add(final double t) {
        final double newSum = sum + t;
        comp += DD.twoSumLow(sum, t, newSum);
        sum = newSum;

        return this;
    }







    public Sum add(final double... terms) {
        for (final double t : terms) {
            add(t);
        }

        return this;
    }








    public Sum addProduct(final double a,
                          final double b) {
        final double ab = a * b;
        final double pLow = ExtendedPrecision.productLow(a, b, ab);

        final double newSum = sum + ab;
        comp += DD.twoSumLow(sum, ab, newSum) + pLow;
        sum = newSum;

        return this;
    }









    public Sum addProducts(final double[] a,
                           final double[] b) {
        final int len = a.length;
        if (len != b.length) {
            throw new IllegalArgumentException("Dimension mismatch: " +
                                               a.length + " != " + b.length);
        }

        for (int i = 0; i < len; ++i) {
            addProduct(a[i], b[i]);
        }

        return this;
    }







    public Sum add(final Sum other) {
        return add(other.sum, other.comp);
    }








    public Sum subtract(final Sum other) {
        return add(-other.sum, -other.comp);
    }















    private Sum add(double s, double c) {


        final DD result = DD.ofSum(sum, comp).add(DD.ofSum(s, c));
        if (result.isFinite()) {
            sum = result.hi();
            comp = result.lo();
        } else {


            add(s);
            if (!Double.isNaN(c)) {
                add(c);
            }
        }
        return this;
    }









    @Override
    public void accept(final double value) {
        add(value);
    }






    @Override
    public double getAsDouble() {

        final double hpsum = sum + comp;
        return Double.isFinite(hpsum) ?
                hpsum :
                sum;
    }






    public static Sum create() {
        return new Sum(0d);
    }







    public static Sum of(final double a) {
        return new Sum(a);
    }







    public static Sum of(final double... values) {
        return create().add(values);
    }








    public static Sum ofProducts(final double[] a,
                                 final double[] b) {
        return create().addProducts(a, b);
    }
}
