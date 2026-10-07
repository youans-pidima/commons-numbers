















package org.apache.commons.numbers.fraction;

import java.io.Serializable;
import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.numbers.core.NativeOperators;











public final class Fraction
    extends Number
    implements Comparable<Fraction>,
               NativeOperators<Fraction>,
               Serializable {

    public static final Fraction ZERO = new Fraction(0);


    public static final Fraction ONE = new Fraction(1);


    private static final long serialVersionUID = 20190701L;


    private static final double DEFAULT_EPSILON = 1e-5;


    private static final int DEFAULT_MAX_ITERATIONS = 100;


    private static final String NOT_FINITE = "Not finite: ";


    private static final long OVERFLOW = 1L << 31;


    private final int numerator;


    private final int denominator;












    private Fraction(int num, int den) {
        if (den == 0) {
            throw new FractionException(FractionException.ERROR_ZERO_DENOMINATOR);
        }

        if (num == den) {
            numerator = 1;
            denominator = 1;
        } else {

            final int p;
            final int q;




            if (((num | den) & 1) == 0) {
                p = num >> 1;
                q = den >> 1;
            } else {
                p = num;
                q = den;
            }



            final int d = ArithmeticUtils.gcd(p, q);
            numerator = p / d;
            denominator = q / d;
        }
    }








    private Fraction(int num) {
        numerator = num;
        denominator = 1;
    }







































    private Fraction(final double value,
                     final double epsilon,
                     final int maxDenominator,
                     final int maxIterations) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(NOT_FINITE + value);
        }



        final double absValue = Math.abs(value);
        double r0 = absValue;
        long a0 = (long) Math.floor(r0);
        if (a0 > OVERFLOW) {
            throw new FractionException(FractionException.ERROR_CONVERSION_OVERFLOW, value, a0, 1);
        }


        if (r0 - a0 <= epsilon) {
            int num = (int) a0;
            int den = 1;

            if (Math.signum(num) != Math.signum(value)) {
                if (num == Integer.MIN_VALUE) {
                    den = -den;
                } else {
                    num = -num;
                }
            }
            this.numerator = num;
            this.denominator = den;
            return;
        }



        final long maxDen = Math.abs((long) maxDenominator);

        long p0 = 1;
        long q0 = 0;
        long p1 = a0;
        long q1 = 1;

        long p2;
        long q2;

        int n = 0;
        boolean stop = false;
        do {
            ++n;
            final double r1 = 1.0 / (r0 - a0);
            final long a1 = (long) Math.floor(r1);
            p2 = (a1 * p1) + p0;
            q2 = (a1 * q1) + q0;

            if (Long.compareUnsigned(p2, OVERFLOW) > 0 ||
                Long.compareUnsigned(q2, OVERFLOW) > 0) {

                if (epsilon == 0.0) {
                    p2 = p1;
                    q2 = q1;
                    break;
                }
                throw new FractionException(FractionException.ERROR_CONVERSION_OVERFLOW, value, p2, q2);
            }

            final double convergent = (double) p2 / q2;
            if (n < maxIterations &&
                Math.abs(convergent - absValue) > epsilon &&
                q2 < maxDen) {
                p0 = p1;
                p1 = p2;
                q0 = q1;
                q1 = q2;
                a0 = a1;
                r0 = r1;
            } else {
                stop = true;
            }
        } while (!stop);

        if (n >= maxIterations) {
            throw new FractionException(FractionException.ERROR_CONVERSION, value, maxIterations);
        }




        int num;
        int den;
        if (q2 <= maxDen) {
            num = (int) p2;
            den = (int) q2;
        } else {
            num = (int) p1;
            den = (int) q1;
        }


        if (Math.signum(num) * Math.signum(den) != Math.signum(value)) {
            if (num == Integer.MIN_VALUE) {
                den = -den;
            } else {
                num = -num;
            }
        }

        this.numerator = num;
        this.denominator = den;
    }









    public static Fraction from(final double value) {
        return from(value, DEFAULT_EPSILON, DEFAULT_MAX_ITERATIONS);
    }




















    public static Fraction from(final double value,
                                final double epsilon,
                                final int maxIterations) {
        if (value == 0) {
            return ZERO;
        }
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Max iterations must be strictly positive: " + maxIterations);
        }
        if (epsilon >= 0) {
            return new Fraction(value, epsilon, Integer.MIN_VALUE, maxIterations);
        }
        throw new IllegalArgumentException("Epsilon must be positive: " + maxIterations);
    }





















    public static Fraction from(final double value,
                                final int maxDenominator) {
        if (value == 0) {
            return ZERO;
        }
        if (maxDenominator == 0) {

            throw new IllegalArgumentException(FractionException.ERROR_ZERO_DENOMINATOR);
        }
        return new Fraction(value, 0, maxDenominator, DEFAULT_MAX_ITERATIONS);
    }







    public static Fraction of(final int num) {
        if (num == 0) {
            return ZERO;
        }
        return new Fraction(num);
    }










    public static Fraction of(final int num, final int den) {
        if (num == 0) {
            return ZERO;
        }
        return new Fraction(num, den);
    }


































    public static Fraction parse(String s) {
        final String stripped = s.replace(",", "");
        final int slashLoc = stripped.indexOf('/');

        if (slashLoc == -1) {
            return of(Integer.parseInt(stripped.trim()));
        }
        final int num = Integer.parseInt(stripped.substring(0, slashLoc).trim());
        final int denom = Integer.parseInt(stripped.substring(slashLoc + 1).trim());
        return of(num, denom);
    }

    @Override
    public Fraction zero() {
        return ZERO;
    }






    @Override
    public boolean isZero() {
        return numerator == 0;
    }

    @Override
    public Fraction one() {
        return ONE;
    }






    @Override
    public boolean isOne() {
        return numerator == denominator;
    }






    public int getNumerator() {
        return numerator;
    }






    public int getDenominator() {
        return denominator;
    }







    public int signum() {
        return Integer.signum(numerator) * Integer.signum(denominator);
    }






    public Fraction abs() {
        return signum() >= 0 ?
            this :
            negate();
    }

    @Override
    public Fraction negate() {
        return numerator == Integer.MIN_VALUE ?
            new Fraction(numerator, -denominator) :
            new Fraction(-numerator, denominator);
    }








    @Override
    public Fraction reciprocal() {
        return new Fraction(denominator, numerator);
    }







    @Override
    public double doubleValue() {
        return numerator / (double) denominator;
    }







    @Override
    public float floatValue() {
        return (float) doubleValue();
    }






    @Override
    public int intValue() {


        return (int) doubleValue();
    }






    @Override
    public long longValue() {
        return (long) numerator / denominator;
    }










    public Fraction add(final int value) {
        if (value == 0) {
            return this;
        }
        if (isZero()) {
            return new Fraction(value);
        }

        final long num = (long) value * denominator;
        return of(Math.toIntExact(numerator + num), denominator);
    }










    @Override
    public Fraction add(Fraction value) {
        return addSub(value, true          );
    }










    public Fraction subtract(final int value) {
        if (value == 0) {
            return this;
        }
        if (isZero()) {

            return value == Integer.MIN_VALUE ?
                new Fraction(Integer.MIN_VALUE, -1) :
                new Fraction(-value);
        }

        final long num = (long) value * denominator;
        return of(Math.toIntExact(numerator - num), denominator);
    }










    @Override
    public Fraction subtract(Fraction value) {
        return addSub(value, false               );
    }










    private Fraction addSub(Fraction value, boolean isAdd) {
        if (value.isZero()) {
            return this;
        }

        if (isZero()) {
            return isAdd ? value : value.negate();
        }







        final int d1 = ArithmeticUtils.gcd(denominator, value.denominator);
        final long uvp = (long) numerator * (value.denominator / d1);
        final long upv = (long) value.numerator * (denominator / d1);










        final long t = isAdd ? uvp + upv : uvp - upv;






        final long d2 = ArithmeticUtils.gcd(t, d1);

        return of(Math.toIntExact(t / d2),
                  Math.multiplyExact(denominator / d1,
                                     value.denominator / (int) d2));
    }










    @Override
    public Fraction multiply(final int value) {
        if (value == 0 || isZero()) {
            return ZERO;
        }




        final int d2 = ArithmeticUtils.gcd(value, denominator);
        return new Fraction(Math.multiplyExact(numerator, value / d2),
                            denominator / d2);
    }










    @Override
    public Fraction multiply(Fraction value) {
        if (value.isZero() || isZero()) {
            return ZERO;
        }
        return multiply(value.numerator, value.denominator);
    }














    private Fraction multiply(int num, int den) {


        final int d1 = ArithmeticUtils.gcd(numerator, den);
        final int d2 = ArithmeticUtils.gcd(num, denominator);
        return new Fraction(Math.multiplyExact(numerator / d1, num / d2),
                            Math.multiplyExact(denominator / d2, den / d1));
    }











    public Fraction divide(final int value) {
        if (value == 0) {
            throw new FractionException(FractionException.ERROR_DIVIDE_BY_ZERO);
        }
        if (isZero()) {
            return ZERO;
        }





        final int d1 = ArithmeticUtils.gcd(numerator, value);
        return new Fraction(numerator / d1,
                            Math.multiplyExact(denominator, value / d1));
    }











    @Override
    public Fraction divide(Fraction value) {
        if (value.isZero()) {
            throw new FractionException(FractionException.ERROR_DIVIDE_BY_ZERO);
        }
        if (isZero()) {
            return ZERO;
        }

        return multiply(value.denominator, value.numerator);
    }









    @Override
    public Fraction pow(final int exponent) {
        if (exponent == 1) {
            return this;
        }
        if (exponent == 0) {
            return ONE;
        }
        if (isZero()) {
            if (exponent < 0) {
                throw new FractionException(FractionException.ERROR_ZERO_DENOMINATOR);
            }
            return ZERO;
        }
        if (exponent > 0) {
            return new Fraction(ArithmeticUtils.pow(numerator, exponent),
                                ArithmeticUtils.pow(denominator, exponent));
        }
        if (exponent == -1) {
            return this.reciprocal();
        }
        if (exponent == Integer.MIN_VALUE) {

            return new Fraction(ArithmeticUtils.pow(denominator, Integer.MAX_VALUE) * denominator,
                                ArithmeticUtils.pow(numerator, Integer.MAX_VALUE) * numerator);
        }
        return new Fraction(ArithmeticUtils.pow(denominator, -exponent),
                            ArithmeticUtils.pow(numerator, -exponent));
    }












    @Override
    public String toString() {
        final String str;
        if (isZero()) {
            str = "0";
        } else if (denominator == 1) {
            str = Integer.toString(numerator);
        } else {
            str = numerator + " / " + denominator;
        }
        return str;
    }







    @Override
    public int compareTo(Fraction other) {

        final int lns = Integer.signum(numerator);
        final int lds = Integer.signum(denominator);
        final int rns = Integer.signum(other.numerator);
        final int rds = Integer.signum(other.denominator);

        final int lhsSigNum = lns * lds;
        final int rhsSigNum = rns * rds;

        if (lhsSigNum != rhsSigNum) {
            return (lhsSigNum > rhsSigNum) ? 1 : -1;
        }


        if (lhsSigNum == 0) {
            return 0;
        }


        final long nOd = (long) numerator * lns * other.denominator * rds;
        final long dOn = (long) denominator * lds * other.numerator * rns;
        return lhsSigNum > 0 ?
            Long.compare(nOd, dOn) :
            Long.compare(dOn, nOd);
    }








    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Fraction) {


            final Fraction rhs = (Fraction) other;
            if (signum() == rhs.signum()) {
                return Math.abs(numerator) == Math.abs(rhs.numerator) &&
                       Math.abs(denominator) == Math.abs(rhs.denominator);
            }
        }

        return false;
    }

    @Override
    public int hashCode() {







        final int numS = Integer.signum(numerator);
        final int denS = Integer.signum(denominator);
        return (31 * (31 + numerator * numS) + denominator * denS) * numS * denS;
    }
}
