















package org.apache.commons.numbers.complex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class UserGuideTest {

    @Test
    void testComplex1() {
        double x = 3;
        double y = 4;
        Complex c1 = Complex.ofCartesian(x, y);


        double rho = 1.23;
        double theta = Math.PI / 2;
        Complex c2 = Complex.ofPolar(rho, theta);


        Assertions.assertNotNull(c1);
        Assertions.assertNotNull(c2);
    }

    @Test
    void testComplex2() {
        Complex c1 = Complex.ofCartesian(3, 4);
        Complex c2 = Complex.ofCartesian(5, 6);
        Complex c3 = c1.multiply(c2).sqrt();

        double magnitude = c3.abs();
        double argument = c3.arg();

        boolean finite = c3.isFinite();


        Assertions.assertTrue(Double.isFinite(magnitude));
        Assertions.assertTrue(Double.isFinite(argument));
        Assertions.assertTrue(finite);
    }
}
