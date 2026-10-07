















package org.apache.commons.numbers.primes;

import java.util.List;









public final class Primes {

    static final String NUMBER_TOO_SMALL = "%d is smaller than the minimum (%d)";




    private Primes() {}











    public static boolean isPrime(int n) {
        if (n < 2) {
            return false;
        }

        for (final int p : SmallPrimes.PRIMES) {
            if (n % p == 0) {
                return n == p;
            }
        }
        return SmallPrimes.millerRabinPrimeTest(n);
    }








    public static int nextPrime(int n) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format(NUMBER_TOO_SMALL, n, 0));
        }
        if (n <= 2) {
            return 2;
        }
        n |= 1;

        if (isPrime(n)) {
            return n;
        }



        final int rem = n % 3;
        if (rem == 0) {
            n += 2;
        } else if (rem == 1) {
            n += 4;
        }
        while (true) {
            if (isPrime(n)) {
                return n;
            }
            n += 2;
            if (isPrime(n)) {
                return n;
            }
            n += 4;
        }
    }








    public static List<Integer> primeFactors(int n) {
        if (n < 2) {
            throw new IllegalArgumentException(String.format(NUMBER_TOO_SMALL, n, 2));
        }
        return SmallPrimes.trialDivision(n);
    }
}
