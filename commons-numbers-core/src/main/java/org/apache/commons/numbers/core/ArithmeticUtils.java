















package org.apache.commons.numbers.core;

import java.math.BigInteger;






public final class ArithmeticUtils {


    private static final String NEGATIVE_EXPONENT_1 = "negative exponent ({";

    private static final String NEGATIVE_EXPONENT_2 = "})";


    private ArithmeticUtils() {

    }






























    public static int gcd(int p, int q) {


        int a = p > 0 ? -p : p;
        int b = q > 0 ? -q : q;

        final int negatedGcd;
        if (a == 0) {
            negatedGcd = b;
        } else if (b == 0) {
            negatedGcd = a;
        } else {

            final int aTwos = Integer.numberOfTrailingZeros(a);
            final int bTwos = Integer.numberOfTrailingZeros(b);
            a >>= aTwos;
            b >>= bTwos;
            final int shift = Math.min(aTwos, bTwos);







            while (a != b) {
                final int delta = a - b;
                b = Math.max(a, b);
                a = delta > 0 ? -delta : delta;


                a >>= Integer.numberOfTrailingZeros(a);
            }


            negatedGcd = a << shift;
        }
        if (negatedGcd == Integer.MIN_VALUE) {
            throw new NumbersArithmeticException("overflow: gcd(%d, %d) is 2^31",
                                                 p, q);
        }
        return -negatedGcd;
    }































    public static long gcd(long p, long q) {


        long a = p > 0 ? -p : p;
        long b = q > 0 ? -q : q;

        final long negatedGcd;
        if (a == 0) {
            negatedGcd = b;
        } else if (b == 0) {
            negatedGcd = a;
        } else {

            final int aTwos = Long.numberOfTrailingZeros(a);
            final int bTwos = Long.numberOfTrailingZeros(b);
            a >>= aTwos;
            b >>= bTwos;
            final int shift = Math.min(aTwos, bTwos);







            while (true) {
                final long delta = a - b;

                if (delta == 0) {




                    break;
                }

                b = Math.max(a, b);
                a = delta > 0 ? -delta : delta;


                a >>= Long.numberOfTrailingZeros(a);
            }


            negatedGcd = a << shift;
        }
        if (negatedGcd == Long.MIN_VALUE) {
            throw new NumbersArithmeticException("overflow: gcd(%d, %d) is 2^63",
                    p, q);
        }
        return -negatedGcd;
    }






















    public static int lcm(int a, int b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        final int lcm = Math.abs(Math.multiplyExact(a / gcd(a, b), b));
        if (lcm == Integer.MIN_VALUE) {
            throw new NumbersArithmeticException("overflow: lcm(%d, %d) is 2^31",
                                                 a, b);
        }
        return lcm;
    }






















    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) {
            return 0;
        }
        final long lcm = Math.abs(Math.multiplyExact(a / gcd(a, b), b));
        if (lcm == Long.MIN_VALUE) {
            throw new NumbersArithmeticException("overflow: lcm(%d, %d) is 2^63",
                                                 a, b);
        }
        return lcm;
    }




















    public static int pow(final int k,
                          final int e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        if (k == 0) {
            return e == 0 ? 1 : 0;
        }

        if (k == 1) {
            return 1;
        }

        if (k == -1) {
            return (e & 1) == 0 ? 1 : -1;
        }

        if (e >= 31) {
            throw new ArithmeticException("integer overflow");
        }

        int exp = e;
        int result = 1;
        int k2p    = k;
        while (true) {
            if ((exp & 0x1) != 0) {
                result = Math.multiplyExact(result, k2p);
            }

            exp >>= 1;
            if (exp == 0) {
                break;
            }

            k2p = Math.multiplyExact(k2p, k2p);
        }

        return result;
    }




















    public static long pow(final long k,
                           final int e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        if (k == 0L) {
            return e == 0 ? 1L : 0L;
        }

        if (k == 1L) {
            return 1L;
        }

        if (k == -1L) {
            return (e & 1) == 0 ? 1L : -1L;
        }

        if (e >= 63) {
            throw new ArithmeticException("long overflow");
        }

        int exp = e;
        long result = 1;
        long k2p    = k;
        while (true) {
            if ((exp & 0x1) != 0) {
                result = Math.multiplyExact(result, k2p);
            }

            exp >>= 1;
            if (exp == 0) {
                break;
            }

            k2p = Math.multiplyExact(k2p, k2p);
        }

        return result;
    }









    public static BigInteger pow(final BigInteger k, int e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        return k.pow(e);
    }









    public static BigInteger pow(final BigInteger k, final long e) {
        if (e < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        long exp = e;
        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (exp != 0) {
            if ((exp & 0x1) != 0) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            exp >>= 1;
        }

        return result;
    }









    public static BigInteger pow(final BigInteger k, final BigInteger e) {
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw new IllegalArgumentException(NEGATIVE_EXPONENT_1 + e + NEGATIVE_EXPONENT_2);
        }

        BigInteger exp = e;
        BigInteger result = BigInteger.ONE;
        BigInteger k2p    = k;
        while (!BigInteger.ZERO.equals(exp)) {
            if (exp.testBit(0)) {
                result = result.multiply(k2p);
            }
            k2p = k2p.multiply(k2p);
            exp = exp.shiftRight(1);
        }

        return result;
    }







    public static boolean isPowerOfTwo(long n) {
        return n > 0 && (n & (n - 1)) == 0;
    }




















    public static int remainderUnsigned(int dividend, int divisor) {
        return Integer.remainderUnsigned(dividend, divisor);
    }




















    public static long remainderUnsigned(long dividend, long divisor) {

        if (divisor < 0) {






            return dividend >= 0 || dividend < divisor ? dividend : dividend - divisor;
        }

        final long q = ((dividend >>> 1) / divisor) << 1;
        final long r = dividend - q * divisor;










        return r - (divisor & (~(r - divisor) >> 63));
    }

























    public static int divideUnsigned(int dividend, int divisor) {
        return Integer.divideUnsigned(dividend, divisor);
    }

























    public static long divideUnsigned(long dividend, long divisor) {




        if (divisor < 0) {


            return (dividend & ~(dividend - divisor)) >>> 63;
        }
        final long q = ((dividend >>> 1) / divisor) << 1;
        final long r = dividend - q * divisor;


        return q + ((r | ~(r - divisor)) >>> 63);
    }




    private static class NumbersArithmeticException extends ArithmeticException {

        private static final long serialVersionUID = 20180130L;








        NumbersArithmeticException(String message, Object... args) {
            super(String.format(message, args));
        }
    }
}
