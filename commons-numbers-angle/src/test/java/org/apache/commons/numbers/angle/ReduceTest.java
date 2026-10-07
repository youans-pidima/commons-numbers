















package org.apache.commons.numbers.angle;

import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class ReduceTest {
    @Test
    void testReduce() {
        final double period = 12.222;
        final double offset = 13456.789;

        final double delta = 1.5;

        double orig = offset + 122456789 * period + delta;
        double expected = delta;

        final Reduce r = new Reduce(offset, period);
        Assertions.assertEquals(expected,
                                r.applyAsDouble(orig),
                                1e-7);

        orig = offset - 123356789 * period - delta;
        expected = Math.abs(period) - delta;
        Assertions.assertEquals(expected,
                                r.applyAsDouble(orig),
                                1e-6);

        orig = offset - 123446789 * period + delta;
        expected = delta;
        Assertions.assertEquals(expected,
                                r.applyAsDouble(orig),
                                1e-6);
    }

    @Test
    void testNaN() {
        final double[] values = new double[] {
            12.345, -9876.5, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
        };

        for (double offset : values) {
            for (double period : values) {
                for (double x : values) {
                    final boolean expectedNaN = Double.isNaN(x) || Double.isInfinite(x) ||
                        Double.isNaN(period) || Double.isInfinite(period) ||
                        Double.isNaN(offset) || Double.isInfinite(offset);

                    final double v = new Reduce(offset, period).applyAsDouble(x);
                    if (expectedNaN) {
                        Assertions.assertTrue(Double.isNaN(v));
                    } else {
                        Assertions.assertFalse(Double.isNaN(v));
                    }
                }
            }
        }
    }

    @Test
    void testReduceNegativePeriod() {
        final double period = 12.222;
        final double offset = 13;
        final double delta = 1.5;
        double orig = offset + 122456789 * period + delta;
        double expected = delta;

        final Reduce r1 = new Reduce(offset, period);
        final Reduce r2 = new Reduce(offset, -period);
        Assertions.assertEquals(expected,
                                r1.applyAsDouble(orig),
                                1e-7);
        Assertions.assertEquals(r1.applyAsDouble(orig),
                                r2.applyAsDouble(orig),
                                0d);
    }

    @Test
    void testReduceComparedWithNormalize() {
        final double period = 2 * Math.PI;
        for (double lo = -15; lo <= 15; lo += 1) {
            final DoubleUnaryOperator n = Angle.Rad.normalizer(lo);
            final Reduce reduce = new Reduce(lo, period);
            for (double a = -15; a <= 15; a += 0.5) {
                final double nA = n.applyAsDouble(a);
                final double r = reduce.applyAsDouble(a) + lo;
                Assertions.assertEquals(nA, r, Math.ulp(nA),
                                        "a=" + a + " lo=" + lo);
            }
        }
    }
}
