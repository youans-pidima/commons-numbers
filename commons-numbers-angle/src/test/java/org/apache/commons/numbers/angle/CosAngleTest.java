















package org.apache.commons.numbers.angle;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class CosAngleTest {

    @Test
    void testCosAngle2D() {
        double expected;

        final double[] v1 = {1, 0};
        expected = 1;
        Assertions.assertEquals(expected, CosAngle.value(v1, v1));

        final double[] v2 = {0, 1};
        expected = 0;
        Assertions.assertEquals(expected, CosAngle.value(v1, v2));

        final double[] v3 = {7, 7};
        expected = Math.sqrt(2) / 2;
        Assertions.assertEquals(expected, CosAngle.value(v1, v3), 1e-15);
        Assertions.assertEquals(expected, CosAngle.value(v3, v2), 1e-15);

        final double[] v4 = {-5, 0};
        expected = -1;
        Assertions.assertEquals(expected, CosAngle.value(v1, v4));

        final double[] v5 = {-100, 100};
        expected = 0;
        Assertions.assertEquals(expected, CosAngle.value(v3, v5));
    }

    @Test
    void testCosAngle3D() {
        double expected;

        final double[] v1 = {1, 1, 0};
        expected = 1;
        Assertions.assertEquals(expected, CosAngle.value(v1, v1), 1e-15);

        final double[] v2 = {1, 1, 1};
        expected = Math.sqrt(2) / Math.sqrt(3);
        Assertions.assertEquals(expected, CosAngle.value(v1, v2), 1e-15);
    }

    @Test
    void testCosAngleExtreme() {
        double expected;

        final double tiny = 1e-200;
        final double[] v1 = {tiny, tiny};
        final double big = 1e200;
        final double[] v2 = {-big, -big};
        expected = -1;
        Assertions.assertEquals(expected, CosAngle.value(v1, v2), 1e-15);

        final double[] v3 = {big, -big};
        expected = 0;
        Assertions.assertEquals(expected, CosAngle.value(v1, v3), 1e-15);
    }

    @Test
    void testCosAngle_dimensionMismatch() {
        final double[] a = {1.0};
        final double[] b = {1.0, 2.0};

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CosAngle.value(a, b);
        });
    }
}
