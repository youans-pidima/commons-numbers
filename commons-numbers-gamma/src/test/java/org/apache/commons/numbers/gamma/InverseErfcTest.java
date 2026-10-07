















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class InverseErfcTest {
    @Test
    void testErfcInvNaN() {
        Assertions.assertTrue(Double.isNaN(InverseErfc.value(-0.001)));
        Assertions.assertTrue(Double.isNaN(InverseErfc.value(+2.001)));
    }

    @Test
    void testErfcInvInfinite() {
        Assertions.assertTrue(Double.isInfinite(InverseErfc.value(-0)));
        Assertions.assertTrue(InverseErfc.value(0) > 0);
        Assertions.assertTrue(Double.isInfinite(InverseErfc.value(+2)));
        Assertions.assertTrue(InverseErfc.value(+2) < 0);
    }

    @Test
    void testErfcInv() {
        for (double x = -5.85; x < 5.9; x += 0.01) {
            final double y = Erfc.value(x);
            final double dydxAbs = 2 * Math.exp(-x * x) / Math.sqrt(Math.PI);
            Assertions.assertEquals(x, InverseErfc.value(y), 1.0e-15 / dydxAbs);
        }
    }
}
