















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class InvGamma1pm1Test {





















    private static final double[][] INV_GAMMA1P_M1_REF = {
        {-0.5, -.4358104164522437},
        {-0.375, -.3029021533379859},
        {-0.25, -0.183951060901737},
        {-0.125, -.08227611018520711},
        {0.0, 0.0},
        {0.125, .06186116458306091},
        {0.25, .1032626513208373},
        {0.375, .1249687649039041},
        {0.5, .1283791670955126},
        {0.625, .1153565546592225},
        {0.75, 0.0880652521310173},
        {0.875, .04882730264547758},
        {1.0, 0.0},
        {1.125, -.05612340925950141},
        {1.25, -.1173898789433302},
        {1.375, -.1818408982517061},
        {1.5, -0.247747221936325},
    };

    @Test
    void testInvGamma1pm1() {
        final int ulps = 3;
        for (int i = 0; i < INV_GAMMA1P_M1_REF.length; i++) {
            final double[] ref = INV_GAMMA1P_M1_REF[i];
            final double x = ref[0];
            final double expected = ref[1];
            final double actual = InvGamma1pm1.value(x);
            final double tol = ulps * Math.ulp(expected);
            Assertions.assertEquals(expected, actual, tol, Double.toString(x));
        }
    }

    @Test
    void testInvGamma1pm1Precondition1() {
        Assertions.assertThrows(GammaException.class,
            () -> InvGamma1pm1.value(-0.51)
        );
    }

    @Test
    void testInvGamma1pm1Precondition2() {
        final double x = Math.nextUp(1.5);
        GammaException ex = Assertions.assertThrows(GammaException.class,
            () -> InvGamma1pm1.value(x)
        );
        Assertions.assertTrue(ex.getMessage().contains(Double.toString(x)),
            () -> ex.getMessage() + " missing " + Double.toString(x));
    }
}
