
















package org.apache.commons.numbers.combinatorics;

import org.apache.commons.numbers.gamma.LogGamma;







public final class LogFactorial {




    private static final int FACTORIALS_CACHE_SIZE = 21;



    private final double[] logFactorials;








    private LogFactorial(int numValues,
                         double[] cache) {
        if (numValues < 0) {
            throw new CombinatoricsException(CombinatoricsException.NEGATIVE, numValues);
        }

        logFactorials = new double[numValues];

        final int beginCopy = 2;
        final int endCopy;
        if (cache == null || cache.length <= beginCopy) {
            endCopy = beginCopy;
        } else {
            endCopy = Math.min(cache.length, numValues);
        }



        if (endCopy - beginCopy > 0) {
            System.arraycopy(cache, beginCopy, logFactorials, beginCopy, endCopy - beginCopy);
        }


        for (int i = endCopy; i < numValues; i++) {
            logFactorials[i] = logFactorials[i - 1] + Math.log(i);
        }
    }





    public static LogFactorial create() {
        return new LogFactorial(0, null);
    }









    public LogFactorial withCache(final int cacheSize) {
        return new LogFactorial(cacheSize, logFactorials);
    }








    public double value(int n) {
        if (n < 0) {
            throw new CombinatoricsException(CombinatoricsException.NEGATIVE, n);
        }


        if (n < logFactorials.length) {
            return logFactorials[n];
        }


        if (n < FACTORIALS_CACHE_SIZE) {
            return Math.log(Factorial.value(n));
        }


        return LogGamma.value(n + 1.0);
    }
}
