
















package org.apache.commons.numbers.combinatorics;







public final class BinomialCoefficientDouble {

    private static final int MAX_FACTORIAL = 170;


    private static final int LIMIT_N_LONG = 66;


    private static final int MAX_M = 514;


    private static final int SMALL_N = 1020;


    private static final int SMALL_M = 37;


    private BinomialCoefficientDouble() {

    }
















    public static double value(int n, int k) {
        if (n <= LIMIT_N_LONG) {

            return BinomialCoefficient.value(n, k);
        }
        final int m = BinomialCoefficient.checkBinomial(n, k);

        if (m == 0) {
            return 1;
        }
        if (m == 1) {
            return n;
        }

        double result;
        if (n <= MAX_FACTORIAL) {


            result = Factorial.uncheckedFactorial(n) /
                     Factorial.uncheckedFactorial(m) /
                     Factorial.uncheckedFactorial(n - m);
        } else {



            if (n <= SMALL_N || m <= SMALL_M) {

                result = 1;
                for (int i = 1; i <= m; i++) {
                    result *= n - m + i;
                    result /= i;
                }
            } else {
                if (m > MAX_M) {
                    return Double.POSITIVE_INFINITY;
                }


                result = 1;
                for (int i = 1; i <= SMALL_M; i++) {
                    result *= n - m + i;
                    result /= i;
                }

                for (int i = SMALL_M + 1; i <= m; i++) {
                    final double next = result * (n - m + i);
                    if (next > Double.MAX_VALUE) {

                        result /= i;
                        result *= n - m + i;
                        if (result > Double.MAX_VALUE) {

                            return Double.POSITIVE_INFINITY;
                        }
                    } else {
                        result = next / i;
                    }
                }
            }
        }

        return Math.floor(result + 0.5);
    }
}
