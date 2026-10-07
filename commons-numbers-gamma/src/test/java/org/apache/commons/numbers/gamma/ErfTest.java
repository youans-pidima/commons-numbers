















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;




class ErfTest {










    @ParameterizedTest
    @CsvSource({
        "0, 0",
        "1.960, 0.95",
        "2.576, 0.99",
        "2.807, 0.995",
        "3.291, 0.999",
    })
    void testErf(double x, double expected) {

        x /= Math.sqrt(2);

        double actual = Erf.value(x);
        Assertions.assertEquals(expected, actual, 1e-5);
        Assertions.assertEquals(1 - expected, Erfc.value(x), 1e-5);

        actual = Erf.value(-x);
        expected = -expected;
        Assertions.assertEquals(expected, actual, 1e-5);
        Assertions.assertEquals(1 - expected, Erfc.value(-x), 1e-5);
    }




    @Test
    void testLargeValues() {
        for (int i = 1; i < 200; i *= 10) {
            double result = Erf.value(i);
            Assertions.assertFalse(Double.isNaN(result));
            Assertions.assertTrue(result > 0 && result <= 1);
            result = Erf.value(-i);
            Assertions.assertFalse(Double.isNaN(result));
            Assertions.assertTrue(result >= -1 && result < 0);
            result = Erfc.value(i);
            Assertions.assertFalse(Double.isNaN(result));
            Assertions.assertTrue(result >= 0 && result < 1);
            result = Erfc.value(-i);
            Assertions.assertFalse(Double.isNaN(result));
            Assertions.assertTrue(result >= 1 && result <= 2);
        }
        Assertions.assertEquals(-1, Erf.value(Double.NEGATIVE_INFINITY));
        Assertions.assertEquals(1, Erf.value(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(2, Erfc.value(Double.NEGATIVE_INFINITY));
        Assertions.assertEquals(0, Erfc.value(Double.POSITIVE_INFINITY));
    }





    @Test
    void testErfGnu() {
        final double tol = 1E-15;
        final double[] gnuValues = new double[] {
            -1, -1, -1, -1, -1,
            -1, -1, -1, -0.99999999999999997848,
            -0.99999999999999264217, -0.99999999999846254017, -0.99999999980338395581, -0.99999998458274209971,
            -0.9999992569016276586, -0.99997790950300141459, -0.99959304798255504108, -0.99532226501895273415,
            -0.96610514647531072711, -0.84270079294971486948, -0.52049987781304653809,  0,
            0.52049987781304653809, 0.84270079294971486948, 0.96610514647531072711, 0.99532226501895273415,
            0.99959304798255504108, 0.99997790950300141459, 0.9999992569016276586, 0.99999998458274209971,
            0.99999999980338395581, 0.99999999999846254017, 0.99999999999999264217, 0.99999999999999997848,
            1,  1,  1,  1,
            1,  1,  1,  1};

        double x = -10;
        for (int i = 0; i < 41; i++) {
            Assertions.assertEquals(gnuValues[i], Erf.value(x), Math.abs(gnuValues[i]) * tol);
            x += 0.5d;
        }
    }
}
