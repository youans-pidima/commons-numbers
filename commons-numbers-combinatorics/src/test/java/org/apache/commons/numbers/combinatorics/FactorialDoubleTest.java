















package org.apache.commons.numbers.combinatorics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




@SuppressWarnings("deprecation")
class FactorialDoubleTest {

    private static final int MAX_N = 170;

    @Test
    void testNonPositiveArgument() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> FactorialDouble.create().value(-1)
        );
    }

    @Test
    void testFactorials() {
        final FactorialDouble f = FactorialDouble.create();
        for (int n = 0; n <= MAX_N; n++) {
            Assertions.assertEquals(Factorial.doubleValue(n), f.value(n));
        }
        for (final int n : new int[] {MAX_N + 1, 678, Integer.MAX_VALUE}) {
            Assertions.assertEquals(Factorial.doubleValue(n), f.value(n));
        }
    }






    @Test
    void testWithCacheReturnsThis() {
        final FactorialDouble f = FactorialDouble.create();
        for (int cacheSize : new int[] {-1, 0, 10000000}) {
            Assertions.assertSame(f, f.withCache(cacheSize));
        }
    }
}
