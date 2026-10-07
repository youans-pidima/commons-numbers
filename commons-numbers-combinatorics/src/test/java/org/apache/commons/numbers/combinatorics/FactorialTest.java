















package org.apache.commons.numbers.combinatorics;

import java.math.BigInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class FactorialTest {

    private static final int MAX_N_LONG = 20;

    private static final int MAX_N_DOUBLE = 170;

    @Test
    void testFactorialZero() {
        Assertions.assertEquals(1, Factorial.value(0), "0!");
        Assertions.assertEquals(1.0, Factorial.doubleValue(0), "0!");
    }

    @Test
    void testFactorialNonPositiveArgument() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> Factorial.value(-1)
        );
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> Factorial.doubleValue(-1)
        );
    }

    @Test
    void testFactorialArgumentTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class,
            () -> Factorial.value(MAX_N_LONG + 1)
        );
    }

    @Test
    void testFactorialDoubleValueArgumentTooLarge() {

        for (long n = MAX_N_DOUBLE + 1; n < Integer.MAX_VALUE; n *= 2) {
            Assertions.assertEquals(Double.POSITIVE_INFINITY, Factorial.doubleValue((int) n));
        }
    }

    @Test
    void testFactorial() {

        long value = 1;
        for (int n = 1; n <= MAX_N_LONG; n++) {

            value *= n;
            Assertions.assertEquals(value, Factorial.value(n));
        }
    }

    @Test
    void testFactorialDoubleValue() {

        BigInteger value = BigInteger.ONE;
        for (int n = 1; n <= MAX_N_DOUBLE; n++) {

            value = value.multiply(BigInteger.valueOf(n));
            Assertions.assertEquals(value.doubleValue(), Factorial.doubleValue(n));
        }
    }
}
