















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testGamma1() {
        double result = Erfc.value(1.23);
        Assertions.assertTrue(0 <= result && result <= 2);


        double a = 1.23;
        double x = 4.56;
        double result1 = IncompleteGamma.Lower.value(a, x);


        double epsilon = 1e-10;
        int maxIterations = 1000;
        double result2 = IncompleteGamma.Lower.value(a, x, epsilon, maxIterations);
        Assertions.assertEquals(result1, result2, result1 * 1e-14);
    }
}
