















package org.apache.commons.numbers.field;

import org.apache.commons.numbers.core.NativeOperators;





public final class FP64 extends Number
    implements NativeOperators<FP64>,
               Comparable<FP64> {

    private static final long serialVersionUID = 1L;


    private static final FP64 ZERO = new FP64(0);

    private static final FP64 ONE = new FP64(1);

    private final double value;




    private FP64(double value) {
        this.value = value;
    }







    public static FP64 of(double value) {
        return new FP64(value);
    }


    @Override
    public FP64 add(FP64 a) {
        return new FP64(value + a.value);
    }


    @Override
    public FP64 negate() {
        return new FP64(-value);
    }


    @Override
    public FP64 multiply(FP64 a) {
        return new FP64(value * a.value);
    }


    @Override
    public FP64 reciprocal() {
        return new FP64(1 / value);
    }


    @Override
    public FP64 subtract(FP64 a) {
        return new FP64(value - a.value);
    }


    @Override
    public FP64 divide(FP64 a) {
        return new FP64(value / a.value);
    }


    @Override
    public FP64 multiply(int n) {
        return new FP64(value * n);
    }


    @Override
    public FP64 pow(int n) {
        if (n == 0) {
            return ONE;
        }

        return new FP64(Math.pow(value, n));
    }


    @Override
    public boolean equals(Object other) {
        if (other instanceof FP64) {
            final FP64 o = (FP64) other;

            return Double.doubleToLongBits(value + 0.0) == Double.doubleToLongBits(o.value + 0.0);
        }
        return false;
    }


    @Override
    public int hashCode() {

        return Double.hashCode(value + 0.0);
    }


    @Override
    public String toString() {
        return Double.toString(value);
    }


    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public byte byteValue() {
        return (byte) value;
    }


    @Override
    public int compareTo(FP64 other) {
        return Double.compare(value, other.value);
    }


    @Override
    public FP64 zero() {
        return ZERO;
    }


    @Override
    public boolean isZero() {
        return value == 0.0;
    }


    @Override
    public FP64 one() {
        return ONE;
    }


    @Override
    public boolean isOne() {
        return value == 1.0;
    }
}
