















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class ErfDifferenceTest {




    @Test
    void testTwoArgumentErf() {
        final double[] xi = new double[] {-2, -1, -0.9, -0.1, 0, 0.1, 0.9, 1, 2};
        for (double x1 : xi) {
            for (double x2 : xi) {
                double a = ErfDifference.value(x1, x2);
                double b = Erf.value(x2) - Erf.value(x1);
                double c = Erfc.value(x1) - Erfc.value(x2);
                Assertions.assertEquals(a, b, 1e-15);
                Assertions.assertEquals(a, c, 1e-15);
            }
        }
    }






    @Test
    void assertCriticalValue() {


        final double x = 0.47693627620446993;
        Assertions.assertEquals(0.5, Erf.value(x));
        Assertions.assertTrue(Erf.value(Math.nextUp(x)) > 0.5);
        Assertions.assertEquals(0.5, Erfc.value(x));
        Assertions.assertTrue(Erfc.value(Math.nextUp(x)) < 0.5);
    }
}
