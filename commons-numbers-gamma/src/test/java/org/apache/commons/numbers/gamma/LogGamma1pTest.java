















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class LogGamma1pTest {
    private static final double[][] LOG_GAMMA1P_REF = {
        {-0.5, .5723649429247001},
        {-0.375, .3608294954889402},
        {-0.25, .2032809514312954},
        {-0.125, .08585870722533433},
        {0.0, 0.0},
        {0.125, -.06002318412603958},
        {0.25, -.09827183642181316},
        {0.375, -.1177552707410788},
        {0.5, -.1207822376352452},
        {0.625, -.1091741337567954},
        {0.75, -.08440112102048555},
        {0.875, -0.0476726853991883},
        {1.0, 0.0},
        {1.125, .05775985153034387},
        {1.25, .1248717148923966},
        {1.375, .2006984603774558},
        {1.5, .2846828704729192},
    };

    @Test
    void testLogGamma1p() {
        final int ulps = 3;
        for (int i = 0; i < LOG_GAMMA1P_REF.length; i++) {
            final double[] ref = LOG_GAMMA1P_REF[i];
            final double x = ref[0];
            final double expected = ref[1];
            final double actual = LogGamma1p.value(x);
            final double tol = ulps * Math.ulp(expected);
            Assertions.assertEquals(expected, actual, tol, Double.toString(x));
        }
    }

    @Test
    void testLogGamma1pPrecondition1() {
        Assertions.assertThrows(GammaException.class,
            () -> LogGamma1p.value(-0.51)
        );
    }

    @Test
    void testLogGamma1pPrecondition2() {
        Assertions.assertThrows(GammaException.class,
            () -> LogGamma1p.value(1.51)
        );
    }
}
