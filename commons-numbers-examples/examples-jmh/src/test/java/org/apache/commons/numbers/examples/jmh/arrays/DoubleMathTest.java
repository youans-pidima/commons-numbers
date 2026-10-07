
















package org.apache.commons.numbers.examples.jmh.arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class DoubleMathTest {
    @Test
    void testGreaterThanLessThan() {
        final double[] values = {0.0, 1.0, Double.POSITIVE_INFINITY, Double.NaN};
        final int[] sign = {-1, 1};
        for (final double a : values) {
            for (final double b : values) {
                for (final int i : sign) {
                    final double x = i * a;
                    for (final int j : sign) {
                        final double y = j * b;
                        Assertions.assertEquals(Double.compare(x, y) > 0, DoubleMath.greaterThan(x, y),
                            () -> x + " > " + y);
                        Assertions.assertEquals(Double.compare(x, y) < 0, DoubleMath.lessThan(x, y),
                            () -> x + " < " + y);
                    }
                }
            }
        }
    }
}
