















package org.apache.commons.numbers.gamma;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;








class IncompleteGammaTest {
    private static final double EPS = Math.ulp(1.0);

    @ParameterizedTest
    @CsvFileSource(resources = "igamma_med_data.csv")
    void testIGammaUpper(double a, double x, double upper) {
        TestUtils.assertEquals(upper, IncompleteGamma.Upper.value(a, x), 10);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "igamma_med_data.csv")
    void testIGammaLower(double a, double x, double upper, double p, double lower) {
        TestUtils.assertEquals(lower, IncompleteGamma.Lower.value(a, x), 7);
    }








    @ParameterizedTest
    @CsvSource(value = {

        "5.0,2.5,21.38827245393963,0.8911780189141513,2.6117275460603704,0.10882198108584876",

        "19.24400520324707,21.168405532836914,4.0308280447358675E15,0.3084240508178698,9.038282597080282E15,0.6915759491821302",

        "664.0791015625,1328.158203125,Infinity,4.90100553385586E-91,Infinity,1.0",

        "0.9759566783905029,1.0735523700714111,0.33659577343416824,0.33179703084688433,0.6778671124302277,0.6682029691531157",

        "0.4912221431732178,0.9824442863464355,0.2840949896471149,0.1575143024618326,1.519518937513272,0.8424856975381674",
    })
    void testIGammaPolicy(double a, double x, double upper, double q, double lower, double p) {

        Assertions.assertThrows(ArithmeticException.class, () -> IncompleteGamma.Upper.value(a, x, EPS, 1), "upper");
        Assertions.assertThrows(ArithmeticException.class, () -> IncompleteGamma.Lower.value(a, x, EPS, 1), "lower");




        if (Double.isFinite(upper)) {
            final double u1 = IncompleteGamma.Upper.value(a, x);
            final double u2 = IncompleteGamma.Upper.value(a, x, 1e-3, Integer.MAX_VALUE);
            assertCloser("upper", upper, u1, u2);
        }
        if (Double.isFinite(lower)) {
            final double l1 = IncompleteGamma.Lower.value(a, x);
            final double l2 = IncompleteGamma.Lower.value(a, x, 1e-3, Integer.MAX_VALUE);
            assertCloser("lower", lower, l1, l2);
        }
    }




    private static void assertCloser(String msg, double expected, double x, double y) {
        final double dx = Math.abs(expected - x);
        final double dy = Math.abs(expected - y);
        Assertions.assertTrue(dx < dy,
            () -> String.format("%s %s : %s (%s) : %s (%s)", msg, expected, x, dx, y, dy));
    }
}
