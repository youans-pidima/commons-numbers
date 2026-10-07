















package org.apache.commons.numbers.combinatorics;

import org.apache.commons.numbers.gamma.LogGamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class LogFactorialTest {
    @Test
    void testNonPositiveArgumentWithCache() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> LogFactorial.create().withCache(-1)
        );
    }

    @Test
    void testNonPositiveArgument() {
        final LogFactorial f = LogFactorial.create();
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> f.value(-1)
        );
    }

    @Test
    void testDelegation() {
        final LogFactorial f = LogFactorial.create();



        for (int i = 21; i < 10000; i++) {
            final double expected = LogGamma.value(i + 1);
            Assertions.assertEquals(
                    expected, f.value(i), 0d, i + "! ");
        }
    }

    @Test
    void testCompareDirectWithoutCache() {



        final int max = 100;
        final LogFactorial f = LogFactorial.create();

        for (int i = 0; i < max; i++) {
            final double expected = logFactorial(i);
            Assertions.assertEquals(
                    expected, f.value(i), 2 * Math.ulp(expected), i + "! ");
        }
    }

    @Test
    void testCompareDirectWithCache() {
        final int max = 1000;
        final LogFactorial f = LogFactorial.create().withCache(max);

        for (int i = 0; i < max; i++) {
            final double expected = logFactorial(i);
            Assertions.assertEquals(
                    expected, f.value(i), 0d, i + "! ");
        }
    }

    @Test
    void testZeroCache() {

        final LogFactorial f = LogFactorial.create().withCache(0);
        Assertions.assertEquals(0, f.value(0));
        Assertions.assertEquals(0, f.value(1));
    }

    @Test
    void testUselessCache() {

        LogFactorial f = LogFactorial.create().withCache(1);
        Assertions.assertEquals(0, f.value(0));
        Assertions.assertEquals(0, f.value(1));

        f = LogFactorial.create().withCache(2);
        Assertions.assertEquals(0, f.value(0));
        Assertions.assertEquals(0, f.value(1));
    }

    @Test
    void testCacheIncrease() {
        final int max = 100;
        final LogFactorial f1 = LogFactorial.create().withCache(max);
        final LogFactorial f2 = f1.withCache(2 * max);

        final int val = max + max / 2;
        final double expected = logFactorial(val);
        Assertions.assertEquals(expected, f2.value(val));
    }

    @Test
    void testCacheDecrease() {
        final int max = 100;
        final LogFactorial f1 = LogFactorial.create().withCache(max);
        final LogFactorial f2 = f1.withCache(max / 2);

        final int val = max / 4;
        final double expected = logFactorial(val);
        Assertions.assertEquals(expected, f2.value(val));
    }


    private double logFactorial(final int n) {
        double logSum = 0;
        for (int i = 2; i <= n; i++) {
            logSum += Math.log(i);
        }
        return logSum;
    }
}
