















package org.apache.commons.numbers.fraction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import org.apache.commons.numbers.core.NativeOperators;











public final class BigFraction
    extends Number
    implements Comparable<BigFraction>,
               NativeOperators<BigFraction>,
               Serializable {

    public static final BigFraction ZERO = new BigFraction(BigInteger.ZERO);


    public static final BigFraction ONE = new BigFraction(BigInteger.ONE);


    private static final long serialVersionUID = 20190701L;


    private static final int DEFAULT_MAX_ITERATIONS = 100;


    private static final String NOT_FINITE = "Not finite: ";


    private static final long OVERFLOW = 1L << 31;


    private final BigInteger numerator;


    private final BigInteger denominator;












    private BigFraction(BigInteger num, BigInteger den) {
        if (den.signum() == 0) {
            throw new FractionException(FractionException.ERROR_ZERO_DENOMINATOR);
        }


        final BigInteger gcd = num.gcd(den);
        if (BigInteger.ONE.compareTo(gcd) < 0) {
            numerator = num.divide(gcd);
            denominator = den.divide(gcd);
        } else {
            numerator = num;
            denominator = den;
        }
    }








    private BigFraction(BigInteger num) {
        numerator = num;
        denominator = BigInteger.ONE;
    }





































    private static BigFraction from(final double value,
                                    final double epsilon,
                                    final int maxDenominator,
                                    final int maxIterations) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(NOT_FINITE + value);
        }
        if (value == 0) {
            return ZERO;
        }



        final double absValue = Math.abs(value);
        double r0 = absValue;
        long a0 = (long) Math.floor(r0);
        if (a0 > OVERFLOW) {
            throw new FractionException(FractionException.ERROR_CONVERSION_OVERFLOW, value, a0, 1);
        }


        if (r0 - a0 <= epsilon) {

            if (value < 0) {
                a0 = -a0;
            }
            return new BigFraction(BigInteger.valueOf(a0));
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

                if (epsilon == 0) {
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


        long num;
        final long den;
        if (q2 <= maxDen) {
            num = p2;
            den = q2;
        } else {
            num = p1;
            den = q1;
        }


        if (Math.signum(num) * Math.signum(den) != Math.signum(value)) {
            num = -num;
        }

        return new BigFraction(BigInteger.valueOf(num),
                               BigInteger.valueOf(den));
    }

























    public static BigFraction from(final double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(NOT_FINITE + value);
        }
        if (value == 0) {
            return ZERO;
        }

        final long bits = Double.doubleToLongBits(value);
        final long sign = bits & 0x8000000000000000L;
        final long exponent = bits & 0x7ff0000000000000L;
        final long mantissa = bits & 0x000fffffffffffffL;


        long m;
        int k;

        if (exponent == 0) {


            m = mantissa;
            k = -1074;
        } else {

            m = mantissa | 0x0010000000000000L;
            k = (int) (exponent >> 52) - 1075;
        }
        if (sign != 0) {
            m = -m;
        }
        while ((m & 0x001ffffffffffffeL) != 0 &&
               (m & 0x1) == 0) {
            m >>= 1;
            ++k;
        }

        return k < 0 ?
            new BigFraction(BigInteger.valueOf(m),
                            BigInteger.ZERO.flipBit(-k)) :
            new BigFraction(BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k)),
                            BigInteger.ONE);
    }



















    public static BigFraction from(final double value,
                                   final double epsilon,
                                   final int maxIterations) {
        if (maxIterations < 1) {
            throw new IllegalArgumentException("Max iterations must be strictly positive: " + maxIterations);
        }
        if (epsilon >= 0) {
            return from(value, epsilon, Integer.MIN_VALUE, maxIterations);
        }
        throw new IllegalArgumentException("Epsilon must be positive: " + maxIterations);
    }





















    public static BigFraction from(final double value,
                                   final int maxDenominator) {
        if (maxDenominator == 0) {

            throw new IllegalArgumentException(FractionException.ERROR_ZERO_DENOMINATOR);
        }
        return from(value, 0, maxDenominator, DEFAULT_MAX_ITERATIONS);
    }








    public static BigFraction of(final int num) {
        if (num == 0) {
            return ZERO;
        }
        return new BigFraction(BigInteger.valueOf(num));
    }







    public static BigFraction of(final long num) {
        if (num == 0) {
            return ZERO;
        }
        return new BigFraction(BigInteger.valueOf(num));
    }








    public static BigFraction of(final BigInteger num) {
        Objects.requireNonNull(num, "numerator");
        if (num.signum() == 0) {
            return ZERO;
        }
        return new BigFraction(num);
    }










    public static BigFraction of(final int num, final int den) {
        if (num == 0) {
            return ZERO;
        }
        return new BigFraction(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }










    public static BigFraction of(final long num, final long den) {
        if (num == 0) {
            return ZERO;
        }
        return new BigFraction(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }











    public static BigFraction of(final BigInteger num, final BigInteger den) {
        if (num.signum() == 0) {
            return ZERO;
        }
        return new BigFraction(num, den);
    }


































    public static BigFraction parse(String s) {
        final String stripped = s.replace(",", "");
        final int slashLoc = stripped.indexOf('/');

        if (slashLoc == -1) {
            return of(new BigInteger(stripped.trim()));
        }
        final BigInteger num = new BigInteger(stripped.substring(0, slashLoc).trim());
        final BigInteger denom = new BigInteger(stripped.substring(slashLoc + 1).trim());
        return of(num, denom);
    }

    @Override
    public BigFraction zero() {
        return ZERO;
    }






    @Override
    public boolean isZero() {
        return numerator.signum() == 0;
    }

    @Override
    public BigFraction one() {
        return ONE;
    }






    @Override
    public boolean isOne() {
        return numerator.equals(denominator);
    }






    public BigInteger getNumerator() {
        return numerator;
    }






    public int getNumeratorAsInt() {
        return numerator.intValue();
    }






    public long getNumeratorAsLong() {
        return numerator.longValue();
    }






    public BigInteger getDenominator() {
        return denominator;
    }






    public int getDenominatorAsInt() {
        return denominator.intValue();
    }






    public long getDenominatorAsLong() {
        return denominator.longValue();
    }







    public int signum() {
        return numerator.signum() * denominator.signum();
    }






    public BigFraction abs() {
        return signum() >= 0 ?
            this :
            negate();
    }

    @Override
    public BigFraction negate() {
        return new BigFraction(numerator.negate(), denominator);
    }








    @Override
    public BigFraction reciprocal() {
        return new BigFraction(denominator, numerator);
    }






    @Override
    public double doubleValue() {
        return Double.longBitsToDouble(toFloatingPointBits(11, 52));
    }






    @Override
    public float floatValue() {
        return Float.intBitsToFloat((int) toFloatingPointBits(8, 23));
    }






    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }






    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }











    public BigDecimal bigDecimalValue() {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator));
    }










    public BigDecimal bigDecimalValue(RoundingMode roundingMode) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), roundingMode);
    }















    public BigDecimal bigDecimalValue(final int scale, RoundingMode roundingMode) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), scale, roundingMode);
    }














    public BigDecimal bigDecimalValue(MathContext mc) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), mc);
    }








    public BigFraction add(final int value) {
        return add(BigInteger.valueOf(value));
    }








    public BigFraction add(final long value) {
        return add(BigInteger.valueOf(value));
    }








    public BigFraction add(final BigInteger value) {
        if (value.signum() == 0) {
            return this;
        }
        if (isZero()) {
            return of(value);
        }

        return of(numerator.add(denominator.multiply(value)), denominator);
    }








    @Override
    public BigFraction add(final BigFraction value) {
        if (value.isZero()) {
            return this;
        }
        if (isZero()) {
            return value;
        }

        final BigInteger num;
        final BigInteger den;

        if (denominator.equals(value.denominator)) {
            num = numerator.add(value.numerator);
            den = denominator;
        } else {
            num = numerator.multiply(value.denominator).add(value.numerator.multiply(denominator));
            den = denominator.multiply(value.denominator);
        }

        if (num.signum() == 0) {
            return ZERO;
        }

        return new BigFraction(num, den);
    }








    public BigFraction subtract(final int value) {
        return subtract(BigInteger.valueOf(value));
    }








    public BigFraction subtract(final long value) {
        return subtract(BigInteger.valueOf(value));
    }








    public BigFraction subtract(final BigInteger value) {
        if (value.signum() == 0) {
            return this;
        }
        if (isZero()) {
            return of(value.negate());
        }

        return of(numerator.subtract(denominator.multiply(value)), denominator);
    }








    @Override
    public BigFraction subtract(final BigFraction value) {
        if (value.isZero()) {
            return this;
        }
        if (isZero()) {
            return value.negate();
        }

        final BigInteger num;
        final BigInteger den;
        if (denominator.equals(value.denominator)) {
            num = numerator.subtract(value.numerator);
            den = denominator;
        } else {
            num = numerator.multiply(value.denominator).subtract(value.numerator.multiply(denominator));
            den = denominator.multiply(value.denominator);
        }

        if (num.signum() == 0) {
            return ZERO;
        }

        return new BigFraction(num, den);
    }








    @Override
    public BigFraction multiply(final int value) {
        if (value == 0 || isZero()) {
            return ZERO;
        }

        return multiply(BigInteger.valueOf(value));
    }








    public BigFraction multiply(final long value) {
        if (value == 0 || isZero()) {
            return ZERO;
        }

        return multiply(BigInteger.valueOf(value));
    }








    public BigFraction multiply(final BigInteger value) {
        if (value.signum() == 0 || isZero()) {
            return ZERO;
        }
        return new BigFraction(value.multiply(numerator), denominator);
    }








    @Override
    public BigFraction multiply(final BigFraction value) {
        if (value.isZero() || isZero()) {
            return ZERO;
        }
        return new BigFraction(numerator.multiply(value.numerator),
                               denominator.multiply(value.denominator));
    }









    public BigFraction divide(final int value) {
        return divide(BigInteger.valueOf(value));
    }









    public BigFraction divide(final long value) {
        return divide(BigInteger.valueOf(value));
    }









    public BigFraction divide(final BigInteger value) {
        if (value.signum() == 0) {
            throw new FractionException(FractionException.ERROR_DIVIDE_BY_ZERO);
        }
        if (isZero()) {
            return ZERO;
        }
        return new BigFraction(numerator, denominator.multiply(value));
    }









    @Override
    public BigFraction divide(final BigFraction value) {
        if (value.isZero()) {
            throw new FractionException(FractionException.ERROR_DIVIDE_BY_ZERO);
        }
        if (isZero()) {
            return ZERO;
        }

        return new BigFraction(numerator.multiply(value.denominator),
                               denominator.multiply(value.numerator));
    }









    @Override
    public BigFraction pow(final int exponent) {
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
            return new BigFraction(numerator.pow(exponent),
                                   denominator.pow(exponent));
        }
        if (exponent == -1) {
            return this.reciprocal();
        }
        if (exponent == Integer.MIN_VALUE) {

            return new BigFraction(denominator.pow(Integer.MAX_VALUE).multiply(denominator),
                                   numerator.pow(Integer.MAX_VALUE).multiply(numerator));
        }




        return new BigFraction(denominator.pow(-exponent),
                               numerator.pow(-exponent));
    }












    @Override
    public String toString() {
        final String str;
        if (isZero()) {
            str = "0";
        } else if (BigInteger.ONE.equals(denominator)) {
            str = numerator.toString();
        } else {
            str = numerator + " / " + denominator;
        }
        return str;
    }







    @Override
    public int compareTo(final BigFraction other) {
        final int lhsSigNum = signum();
        final int rhsSigNum = other.signum();

        if (lhsSigNum != rhsSigNum) {
            return (lhsSigNum > rhsSigNum) ? 1 : -1;
        }


        if (lhsSigNum == 0) {
            return 0;
        }

        final BigInteger nOd = numerator.abs().multiply(other.denominator.abs());
        final BigInteger dOn = denominator.abs().multiply(other.numerator.abs());
        return lhsSigNum > 0 ?
            nOd.compareTo(dOn) :
            dOn.compareTo(nOd);
    }








    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof BigFraction) {


            final BigFraction rhs = (BigFraction) other;
            if (signum() == rhs.signum()) {
                return numerator.abs().equals(rhs.numerator.abs()) &&
                       denominator.abs().equals(rhs.denominator.abs());
            }
        }

        return false;
    }

    @Override
    public int hashCode() {







        final int numS = numerator.signum();
        final int denS = denominator.signum();
        return (31 * (31 + numerator.hashCode() * numS) + denominator.hashCode() * denS) * numS * denS;
    }
























    private long toFloatingPointBits(int exponentLength, int significandLength) {






        if (isZero()) {
            return 0L;
        }

        final long sign = (numerator.signum() * denominator.signum()) == -1 ? 1L : 0L;
        final BigInteger positiveNumerator = numerator.abs();
        final BigInteger positiveDenominator = denominator.abs();














        final int denRightShift = positiveDenominator.getLowestSetBit();
        final BigInteger divisor = positiveDenominator.shiftRight(denRightShift);





















        int numRightShift = positiveNumerator.bitLength() - divisor.bitLength() - (significandLength + 2);
        if (numRightShift > 0 &&
            divisor.equals(BigInteger.ONE)) {
            numRightShift = Math.min(numRightShift, positiveNumerator.getLowestSetBit());
        }
        final BigInteger dividend = positiveNumerator.shiftRight(numRightShift);

        final BigInteger quotient = dividend.divide(divisor);

        int quotRightShift = quotient.bitLength() - (significandLength + 1);
        long significand = roundAndRightShift(
                quotient,
                quotRightShift,
                !divisor.equals(BigInteger.ONE)
        ).longValue();





        if ((significand & (1L << (significandLength + 1))) != 0) {
            significand >>= 1;
            quotRightShift++;
        }













        final int exponentBias = (1 << (exponentLength - 1)) - 1;
        long exponent = (long) numRightShift - denRightShift + quotRightShift + significandLength + exponentBias;
        final long maxExponent = (1L << exponentLength) - 1L;

        if (exponent >= maxExponent) {
            exponent = maxExponent;
            significand = 0L;
        } else if (exponent > 0) {
            significand &= -1L >>> (64 - significandLength);
        } else {





















            significand = roundAndRightShift(quotient,
                                             (1 - exponentBias - significandLength) - (numRightShift - denRightShift),
                                             !divisor.equals(BigInteger.ONE)).longValue();
            exponent = 0L;










        }

        return (sign << (significandLength + exponentLength)) |
            (exponent << significandLength) |
            significand;
    }















    private static BigInteger roundAndRightShift(BigInteger value, int bits, boolean hasFractionalBits) {



        BigInteger result = value.shiftRight(bits);
        if (value.testBit(bits - 1) &&
            (hasFractionalBits ||
             value.getLowestSetBit() < bits - 1 ||
             value.testBit(bits))) {
            result = result.add(BigInteger.ONE);
        }

        return result;
    }
}
