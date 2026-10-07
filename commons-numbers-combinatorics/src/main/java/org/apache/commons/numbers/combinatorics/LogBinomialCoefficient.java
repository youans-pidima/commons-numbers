
















package org.apache.commons.numbers.combinatorics;

import org.apache.commons.numbers.gamma.LogBeta;







public final class LogBinomialCoefficient {


    private static final int LIMIT_N_LONG = 66;


    private static final int LIMIT_N_DOUBLE = 1029;


    private static final int LIMIT_M_DOUBLE = 37;


    private LogBinomialCoefficient() {

    }











    public static double value(int n, int k) {
        final int m = BinomialCoefficient.checkBinomial(n, k);

        if (m == 0) {
            return 0;
        }
        if (m == 1) {
            return Math.log(n);
        }

        if (n <= LIMIT_N_LONG) {

            return Math.log(BinomialCoefficient.value(n, k));
        }
        if (n <= LIMIT_N_DOUBLE || m <= LIMIT_M_DOUBLE) {

            return Math.log(BinomialCoefficientDouble.value(n, k));
        }











        return -Math.log(m) - LogBeta.value(m, n - m + 1);
    }
}
