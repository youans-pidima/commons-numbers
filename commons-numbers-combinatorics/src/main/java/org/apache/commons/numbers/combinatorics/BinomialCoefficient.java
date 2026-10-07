
















package org.apache.commons.numbers.combinatorics;

import org.apache.commons.numbers.core.ArithmeticUtils;







public final class BinomialCoefficient {


    private static final int MAX_M = 33;


    private static final int SMALL_N = 61;


    private static final int LIMIT_N = 66;


    private BinomialCoefficient() {

    }


















    public static long value(int n, int k) {
        final int m = checkBinomial(n, k);

        if (m == 0) {
            return 1;
        }
        if (m == 1) {
            return n;
        }






        long result = 1;
        if (n <= SMALL_N) {

            int i = n - m + 1;
            for (int j = 1; j <= m; j++) {
                result = result * i / j;
                i++;
            }
        } else if (n <= LIMIT_N) {


            int i = n - m + 1;
            for (int j = 1; j <= m; j++) {






                final long d = ArithmeticUtils.gcd(i, j);
                result = (result / (j / d)) * (i / d);
                ++i;
            }
        } else {
            if (m > MAX_M) {
                throw new ArithmeticException(n + " choose " + k);
            }




            int i = n - m + 1;
            for (int j = 1; j <= m; j++) {
                final long d = ArithmeticUtils.gcd(i, j);
                result = Math.multiplyExact(result / (j / d), i / d);
                ++i;
            }
        }

        return result;
    }














    static int checkBinomial(int n,
                             int k) {



        final int m = n - k;

        if ((n | k | m) < 0) {

            if (n < 0) {
                throw new CombinatoricsException(CombinatoricsException.NEGATIVE, n);
            }
            throw new CombinatoricsException(CombinatoricsException.OUT_OF_RANGE, k, 0, n);
        }
        return m < k ? m : k;
    }
}
