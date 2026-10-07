















package org.apache.commons.numbers.field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;




class FP64Test {
    @ParameterizedTest
    @ValueSource(doubles = {-5.67e89, -0.0, 0.0, Double.POSITIVE_INFINITY})
    void testConsistencyWithDouble(double v) {
        final Double a = Double.valueOf(v);
        final FP64 b = FP64.of(v);

        Assertions.assertEquals(a.doubleValue(), b.doubleValue());
        Assertions.assertEquals(a.floatValue(), b.floatValue());
        Assertions.assertEquals(a.intValue(), b.intValue());
        Assertions.assertEquals(a.longValue(), b.longValue());
        Assertions.assertEquals(a.byteValue(), b.byteValue());
        Assertions.assertEquals(a.toString(), b.toString());

        if (v == 0) {
            Assertions.assertEquals(Double.valueOf(0.0).hashCode(), b.hashCode());
        } else {
            Assertions.assertEquals(a.hashCode(), b.hashCode());
        }
    }

    @Test
    void testEquals() {
        final FP64 a = FP64.of(1.23);
        final FP64 b = FP64.of(4.56);


        Assertions.assertEquals(a, a);

        Assertions.assertEquals(a, FP64.of(a.doubleValue()));

        Assertions.assertNotEquals(a, b);

        Assertions.assertNotEquals(a, new Object());
        Assertions.assertNotEquals(a, null);
    }

    @ParameterizedTest
    @ValueSource(doubles = {1.23, 0.0, -0.0})
    void testEqualsCloseValues(double value) {

        final FP64 a = FP64.of(value);
        final FP64 b = FP64.of(Math.nextUp(value));


        Assertions.assertEquals(a, FP64.of(value));

        Assertions.assertNotEquals(a, b);
    }

    @Test
    void testEqualsZero() {
        final FP64 a = FP64.of(-0.0);
        final FP64 b = FP64.of(0.0);
        Assertions.assertEquals(a, b);
        Assertions.assertEquals(b, a);
        Assertions.assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-3.4, -0.0, 0.0, Double.POSITIVE_INFINITY})
    void testOne(double value) {
        final FP64 a = FP64.of(value);
        Assertions.assertEquals(1d, a.one().doubleValue());

        Assertions.assertEquals(a, a.one().multiply(a));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-3.4, -0.0, 0.0, Double.POSITIVE_INFINITY})
    void testZero(double value) {
        final FP64 a = FP64.of(value);
        Assertions.assertEquals(0d, a.zero().doubleValue());

        Assertions.assertEquals(a, a.zero().add(a));
    }

    @Test
    void testIsOne() {
        Assertions.assertTrue(FP64.of(1.0).isOne());
        Assertions.assertTrue(FP64.of(0.5).add(FP64.of(0.5)).isOne());
        FP64 value = FP64.of(1e300);
        Assertions.assertTrue(value.divide(value).isOne());

        Assertions.assertFalse(FP64.of(3).isOne());
        Assertions.assertFalse(FP64.of(0.0).isOne());
    }

    @Test
    void testIsZero() {
        Assertions.assertTrue(FP64.of(0.0).isZero());
        Assertions.assertTrue(FP64.of(0.5).subtract(FP64.of(0.5)).isZero());
        FP64 value = FP64.of(1e300);
        Assertions.assertTrue(value.multiply(value.zero()).isZero());

        Assertions.assertFalse(FP64.of(3).isZero());
        Assertions.assertFalse(FP64.of(1.0).isZero());
    }

    @Test
    void testSubtract() {
        final double a = 123.4;
        final double b = 5678.9;

        Assertions.assertEquals(a - b, FP64.of(a).subtract(FP64.of(b)).doubleValue());
    }
    @Test
    void testDivide() {
        final double a = 123.4;
        final double b = 5678.9;

        Assertions.assertEquals(a / b, FP64.of(a).divide(FP64.of(b)).doubleValue());
    }

    @Test
    void testMultiplyInt() {
        final double a = 123.4;
        final int n = 3456789;

        Assertions.assertEquals(n * a, FP64.of(a).multiply(n).doubleValue());
    }

    @Test
    void testPowInt() {
        final double a = 123.4;
        final int n = 5;

        Assertions.assertEquals(Math.pow(a, n), FP64.of(a).pow(n).doubleValue());
    }
    @Test
    void testZeroPow() {
        Assertions.assertSame(FP64.of(9876.5).one(), FP64.of(2.3456).pow(0));
    }

    @Test
    void testCompare() {
        Assertions.assertTrue(FP64.of(0).compareTo(FP64.of(-1)) > 0);
        Assertions.assertTrue(FP64.of(1).compareTo(FP64.of(2)) < 0);

        final double v = 123.45;
        Assertions.assertEquals(0, FP64.of(v).compareTo(FP64.of(v)));
    }
}
