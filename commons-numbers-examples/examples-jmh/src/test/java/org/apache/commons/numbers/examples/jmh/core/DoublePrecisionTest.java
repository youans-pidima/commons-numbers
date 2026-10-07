















package org.apache.commons.numbers.examples.jmh.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class DoublePrecisionTest {
    @Test
    void testSplitAssumptions() {

        final double scale = (1 << 27) + 1;

        final double limit = 0x1.0p996;
        Assertions.assertTrue(Double.isFinite(limit * scale));
        Assertions.assertTrue(Double.isFinite(-limit * scale));

        Assertions.assertEquals(Double.POSITIVE_INFINITY, limit * 2 * scale);
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, -limit * 2 * scale);


        Assertions.assertTrue(Math.getExponent(2 * Math.sqrt(Double.MAX_VALUE)) - 2 > 508);
    }

    @Test
    void testHighPart() {
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPart(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPart(Double.NEGATIVE_INFINITY));
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPart(Double.NaN));

        Assertions.assertTrue(Double.isFinite(DoublePrecision.highPart(Double.MAX_VALUE)));
        Assertions.assertTrue(Double.isFinite(DoublePrecision.highPart(-Double.MAX_VALUE)));
    }

    @Test
    void testHighPartUnscaled() {
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPartUnscaled(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPartUnscaled(Double.NEGATIVE_INFINITY));
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPartUnscaled(Double.NaN));

        Assertions.assertEquals(Double.NaN, DoublePrecision.highPartUnscaled(Double.MAX_VALUE));
        Assertions.assertEquals(Double.NaN, DoublePrecision.highPartUnscaled(-Double.MAX_VALUE));
    }





    @Test
    void testProductLow() {
        assertProductLow(0.0, 1.0, Math.nextDown(Double.MIN_NORMAL));
        assertProductLow(0.0, -1.0, Math.nextDown(Double.MIN_NORMAL));
        assertProductLow(Double.NaN, 1.0, Double.POSITIVE_INFINITY);
        assertProductLow(Double.NaN, 1.0, Double.NEGATIVE_INFINITY);
        assertProductLow(Double.NaN, 1.0, Double.NaN);
        assertProductLow(0.0, 1.0, Double.MAX_VALUE);
        assertProductLow(Double.NaN, 2.0, Double.MAX_VALUE);
    }

    private static void assertProductLow(double expected, double x, double y) {

        Assertions.assertEquals(expected, DoublePrecision.productLow(x, y, x * y), 0.0);
    }

    @Test
    void testIsNotNormal() {
        for (double a : new double[] {Double.MAX_VALUE, 1.0, Double.MIN_NORMAL}) {
            Assertions.assertFalse(DoublePrecision.isNotNormal(a));
            Assertions.assertFalse(DoublePrecision.isNotNormal(-a));
        }
        for (double a : new double[] {Double.POSITIVE_INFINITY, 0.0,
                                      Math.nextDown(Double.MIN_NORMAL), Double.NaN}) {
            Assertions.assertTrue(DoublePrecision.isNotNormal(a));
            Assertions.assertTrue(DoublePrecision.isNotNormal(-a));
        }
    }
}
