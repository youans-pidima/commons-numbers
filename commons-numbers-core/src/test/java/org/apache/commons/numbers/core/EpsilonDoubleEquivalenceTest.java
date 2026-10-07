















package org.apache.commons.numbers.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;





class EpsilonDoubleEquivalenceTest {
    @Test
    void testInvalidEpsilonValues() {

        Assertions.assertThrows(IllegalArgumentException.class, () -> Precision.doubleEquivalenceOfEpsilon(-1d));

        String msg;

        msg = Assertions.assertThrows(IllegalArgumentException.class,
            () -> Precision.doubleEquivalenceOfEpsilon(Double.NaN)).getMessage();
        Assertions.assertEquals("Invalid epsilon value: NaN", msg);

        msg = Assertions.assertThrows(IllegalArgumentException.class,
            () -> Precision.doubleEquivalenceOfEpsilon(Double.POSITIVE_INFINITY)).getMessage();
        Assertions.assertEquals("Invalid epsilon value: Infinity", msg);

        msg = Assertions.assertThrows(IllegalArgumentException.class,
            () -> Precision.doubleEquivalenceOfEpsilon(Double.NEGATIVE_INFINITY)).getMessage();
        Assertions.assertEquals("Invalid epsilon value: -Infinity", msg);
    }

    @Test
    void testSignum() {

        final double eps = 1e-2;

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(eps);


        Assertions.assertEquals(Double.POSITIVE_INFINITY, 1 / cmp.signum(0.0), 0d);
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, 1 / cmp.signum(-0.0), 0d);

        Assertions.assertEquals(Double.POSITIVE_INFINITY, 1 / cmp.signum(eps), 0d);
        Assertions.assertEquals(Double.NEGATIVE_INFINITY, 1 / cmp.signum(-eps), 0d);

        Assertions.assertEquals(1, cmp.signum(Math.nextUp(eps)), 0d);
        Assertions.assertEquals(-1, cmp.signum(Math.nextDown(-eps)), 0d);

        Assertions.assertTrue(Double.isNaN(cmp.signum(Double.NaN)));
        Assertions.assertEquals(1, cmp.signum(Double.POSITIVE_INFINITY), 0d);
        Assertions.assertEquals(-1, cmp.signum(Double.NEGATIVE_INFINITY), 0d);
    }

    @Test
    void testCompare_simple() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-10);


        Assertions.assertEquals(0, cmp.compare(1, 1));
        Assertions.assertEquals(-1, cmp.compare(1, 2));
        Assertions.assertEquals(1, cmp.compare(2, 1));

        Assertions.assertEquals(0, cmp.compare(-1, -1));
        Assertions.assertEquals(1, cmp.compare(-1, -2));
        Assertions.assertEquals(-1, cmp.compare(-2, -1));
    }

    @Test
    void testCompare_compareToZero() {

        final double eps = 1e-2;

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(eps);


        Assertions.assertEquals(0, cmp.compare(0.0, 0.0));
        Assertions.assertEquals(0, cmp.compare(+0.0, -0.0));
        Assertions.assertEquals(0, cmp.compare(eps, -0.0));
        Assertions.assertEquals(0, cmp.compare(+0.0, eps));

        Assertions.assertEquals(0, cmp.compare(-eps, -0.0));
        Assertions.assertEquals(0, cmp.compare(+0.0, -eps));

        Assertions.assertEquals(-1, cmp.compare(0.0, 1.0));
        Assertions.assertEquals(1, cmp.compare(1.0, 0.0));

        Assertions.assertEquals(1, cmp.compare(0.0, -1.0));
        Assertions.assertEquals(-1, cmp.compare(-1.0, 0.0));
    }

    @Test
    void testCompare_compareNonZero() {

        final double eps = 1e-5;
        final double small = 1e-3;
        final double big = 1e100;

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(eps);


        Assertions.assertEquals(0, cmp.compare(eps, 2 * eps));
        Assertions.assertEquals(0, cmp.compare(-2 * eps, -eps));

        Assertions.assertEquals(0, cmp.compare(small, small + (0.9 * eps)));
        Assertions.assertEquals(0, cmp.compare(-small - (0.9 * eps), -small));

        Assertions.assertEquals(0, cmp.compare(big, nextUp(big, 1)));
        Assertions.assertEquals(0, cmp.compare(nextDown(-big, 1), -big));

        Assertions.assertEquals(-1, cmp.compare(small, small + (1.1 * eps)));
        Assertions.assertEquals(1, cmp.compare(-small, -small - (1.1 * eps)));

        Assertions.assertEquals(-1, cmp.compare(big, nextUp(big, 2)));
        Assertions.assertEquals(1, cmp.compare(-big, nextDown(-big, 2)));
    }

    @Test
    void testCompare_NaN() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertEquals(-1, cmp.compare(0, Double.NaN));
        Assertions.assertEquals(1, cmp.compare(Double.NaN, 0));
        Assertions.assertEquals(0, cmp.compare(Double.NaN, Double.NaN));

        Assertions.assertEquals(-1, cmp.compare(Double.POSITIVE_INFINITY, Double.NaN));
        Assertions.assertEquals(1, cmp.compare(Double.NaN, Double.POSITIVE_INFINITY));

        Assertions.assertEquals(-1, cmp.compare(Double.NEGATIVE_INFINITY, Double.NaN));
        Assertions.assertEquals(1, cmp.compare(Double.NaN, Double.NEGATIVE_INFINITY));
    }

    @Test
    void testCompare_infinity() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertEquals(-1, cmp.compare(0, Double.POSITIVE_INFINITY));
        Assertions.assertEquals(1, cmp.compare(Double.POSITIVE_INFINITY, 0));
        Assertions.assertEquals(0, cmp.compare(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));

        Assertions.assertEquals(1, cmp.compare(0, Double.NEGATIVE_INFINITY));
        Assertions.assertEquals(-1, cmp.compare(Double.NEGATIVE_INFINITY, 0));
        Assertions.assertEquals(0, cmp.compare(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
    }

    @Test
    void testEq() {

        final double eps = Math.ulp(1.0);
        final double twoEps = 2 * eps;
        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(eps);


        Assertions.assertTrue(cmp.eq(0.0, 0.0));

        Assertions.assertTrue(cmp.eq(1.0, 1.0));
        Assertions.assertTrue(cmp.eq(1.0, 1.0 + eps));
        Assertions.assertTrue(cmp.eq(1.0, 1.0 - eps));
        Assertions.assertFalse(cmp.eq(1.0, 1.0 + twoEps));
        Assertions.assertFalse(cmp.eq(1.0, 1.0 - twoEps));

        Assertions.assertTrue(cmp.eq(-1.0, -1.0));
        Assertions.assertTrue(cmp.eq(-1.0, -1.0 + eps));
        Assertions.assertTrue(cmp.eq(-1.0, -1.0 - eps));
        Assertions.assertFalse(cmp.eq(-1.0, -1.0 + twoEps));
        Assertions.assertFalse(cmp.eq(-1.0, -1.0 - twoEps));
    }

    @Test
    void testEqZero() {

        final double eps = 1e-6;
        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(eps);


        Assertions.assertTrue(cmp.eqZero(0.0));

        Assertions.assertFalse(cmp.eqZero(Math.nextUp(eps)));
        Assertions.assertFalse(cmp.eqZero(Math.nextDown(-eps)));
    }

    @Test
    void testLt() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertTrue(cmp.lt(1, 2));
        Assertions.assertTrue(cmp.lt(-2, -1));

        Assertions.assertFalse(cmp.lt(1, 1));
        Assertions.assertFalse(cmp.lt(-1, -1));
        Assertions.assertFalse(cmp.lt(2, 1));
        Assertions.assertFalse(cmp.lt(-1, -2));
    }

    @Test
    void testLte() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertTrue(cmp.lte(1, 2));
        Assertions.assertTrue(cmp.lte(-2, -1));
        Assertions.assertTrue(cmp.lte(1, 1));
        Assertions.assertTrue(cmp.lte(-1, -1));

        Assertions.assertFalse(cmp.lte(2, 1));
        Assertions.assertFalse(cmp.lte(-1, -2));
    }

    @Test
    void testGt() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertTrue(cmp.gt(2, 1));
        Assertions.assertTrue(cmp.gt(-1, -2));

        Assertions.assertFalse(cmp.gt(1, 1));
        Assertions.assertFalse(cmp.gt(-1, -1));
        Assertions.assertFalse(cmp.gt(1, 2));
        Assertions.assertFalse(cmp.gt(-2, -1));
    }

    @Test
    void testGte() {

        final Precision.DoubleEquivalence cmp = Precision.doubleEquivalenceOfEpsilon(1e-6);


        Assertions.assertTrue(cmp.gte(2, 1));
        Assertions.assertTrue(cmp.gte(-1, -2));
        Assertions.assertTrue(cmp.gte(1, 1));
        Assertions.assertTrue(cmp.gte(-1, -1));

        Assertions.assertFalse(cmp.gte(1, 2));
        Assertions.assertFalse(cmp.gte(-2, -1));
    }








    private static double nextUp(final double n, final int count) {
        double result = n;
        for (int i = 0; i < count; ++i) {
            result = Math.nextUp(result);
        }

        return result;
    }








    private static double nextDown(final double n, final int count) {
        double result = n;
        for (int i = 0; i < count; ++i) {
            result = Math.nextDown(result);
        }

        return result;
    }
}
