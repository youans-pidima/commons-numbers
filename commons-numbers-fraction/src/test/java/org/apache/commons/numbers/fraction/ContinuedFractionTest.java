















package org.apache.commons.numbers.fraction;

import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;




class ContinuedFractionTest {

    private static final double GOLDEN_RATIO = 1.618033988749894848204586834365638117720309;



















    private static final class GoldenRatio extends ContinuedFraction {
        private static final GoldenRatio INSTANCE = new GoldenRatio();




        static GoldenRatio getInstance() {
            return INSTANCE;
        }


        @Override
        public double getA(int n, double x) {
            return 1;
        }


        @Override
        public double getB(int n, double x) {
            return 1;
        }
    }

    @Test
    void testGoldenRatio() {
        final double eps = 1e-8;
        final double gr = GoldenRatio.getInstance().evaluate(0, eps);
        Assertions.assertEquals(GOLDEN_RATIO, gr, GOLDEN_RATIO * eps);
    }





    @Test
    void testGoldenRatioReciprocal() {
        final double eps = 1e-8;
        final ContinuedFraction cf = new ContinuedFraction() {
            @Override
            public double getA(int n, double x) {

                Assertions.assertNotEquals(0, n, "a0 should never require evaluation");
                return 1;
            }

            @Override
            public double getB(int n, double x) {

                return n == 0 ? 0 : 1;
            }
        };
        final double gr = cf.evaluate(0, eps);
        Assertions.assertEquals(1 / GOLDEN_RATIO, gr, eps / GOLDEN_RATIO);
    }







    @ParameterizedTest
    @ValueSource(doubles = {0, -1, Double.NaN})
    void testGoldenRatioEpsilonZero(double epsilon) {


        final double tolerance = Math.ulp(GOLDEN_RATIO);
        Assertions.assertEquals(GOLDEN_RATIO, GoldenRatio.getInstance().evaluate(0, epsilon), tolerance);
    }

    @Test
    void test415Over93() {










        final ContinuedFraction cf = new ContinuedFraction() {
            @Override
            public double getA(int n, double x) {
                return n <= 3 ? 1 : 0;
            }

            @Override
            public double getB(int n, double x) {
                switch (n) {
                case 0:
                    return 4;
                case 1:
                    return 2;
                case 2:
                    return 6;
                case 3:
                    return 7;
                default:
                    return 1;
                }
            }
        };

        final double eps = 1e-8;
        final double gr = cf.evaluate(0, eps, 5);
        Assertions.assertEquals(415.0 / 93.0, gr, eps);
    }

    @Test
    void testMaxIterationsThrows() {
        final ContinuedFraction cf = GoldenRatio.getInstance();

        final double eps = 1e-8;
        final int maxIterations = 3;
        final Throwable t = Assertions.assertThrows(FractionException.class, () -> cf.evaluate(0, eps, maxIterations));
        assertExceptionMessageContains(t, "max");
    }

    @Test
    void testNaNThrows() {

        final ContinuedFraction cf = new ContinuedFraction() {
            @Override
            public double getA(int n, double x) {
                return 1;
            }

            @Override
            public double getB(int n, double x) {
                return n == 0 ? 1 : Double.NaN;
            }
        };

        final double eps = 1e-8;
        final Throwable t = Assertions.assertThrows(FractionException.class, () -> cf.evaluate(0, eps, 5));
        assertExceptionMessageContains(t, "nan");
    }

    @Test
    void testInfThrows() {


        final ContinuedFraction cf = new ContinuedFraction() {
            @Override
            public double getA(int n, double x) {
                return n == 0 ? 1 : Double.MAX_VALUE;
            }

            @Override
            public double getB(int n, double x) {
                return 0.5;
            }
        };

        final double eps = 1e-8;
        final Throwable t = Assertions.assertThrows(FractionException.class, () -> cf.evaluate(0, eps, 5));
        assertExceptionMessageContains(t, "infinity");
    }

    private static void assertExceptionMessageContains(Throwable t, String text) {
        Assertions.assertTrue(t.getMessage().toLowerCase(Locale.ROOT).contains(text),
            () -> "Missing '" + text + "' from exception message: " + t.getMessage());
    }


    @Test
    void testOneIteration() {
        final double eps = 0.5;
        final double gr = GoldenRatio.getInstance().evaluate(0, eps, 1);

        Assertions.assertEquals(2.0, gr);
    }


    @Test
    void testTwoIterations() {
        final double eps = 0.25;
        final double gr = GoldenRatio.getInstance().evaluate(0, eps, 2);

        Assertions.assertEquals(1.5, gr);
    }
}
