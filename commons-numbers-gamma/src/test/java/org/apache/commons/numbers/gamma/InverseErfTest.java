















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class InverseErfTest {
    @Test
    void testErfInvNaN() {
        Assertions.assertTrue(Double.isNaN(InverseErf.value(-1.001)));
        Assertions.assertTrue(Double.isNaN(InverseErf.value(+1.001)));
        Assertions.assertTrue(Double.isNaN(InverseErf.value(Double.NaN)));
    }

    @Test
    void testErfInvInfinite() {
        Assertions.assertTrue(Double.isInfinite(InverseErf.value(-1)));
        Assertions.assertTrue(InverseErf.value(-1) < 0);
        Assertions.assertTrue(Double.isInfinite(InverseErf.value(+1)));
        Assertions.assertTrue(InverseErf.value(+1) > 0);
    }

    @Test
    void testErfInv() {
        for (double x = -5.9; x < 5.9; x += 0.01) {
            final double y = Erf.value(x);
            final double dydx = 2 * Math.exp(-x * x) / Math.sqrt(Math.PI);
            Assertions.assertEquals(x, InverseErf.value(y), 1.0e-15 / dydx);
        }
    }
}
