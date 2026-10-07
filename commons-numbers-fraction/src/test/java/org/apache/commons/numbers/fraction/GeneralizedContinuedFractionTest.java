















package org.apache.commons.numbers.fraction;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction.Coefficient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;




class GeneralizedContinuedFractionTest {

    private static final double GOLDEN_RATIO = 1.618033988749894848204586834365638117720309;

    private static final double INV_GOLDEN_RATIO = 0.618033988749894848204586834365638117720309;

    @Test
    void testRelativeErrorAssumptions() {
        Assertions.assertEquals(0x1.0p-52, 1 / (1.0 - 0x1.0p-53) - 1, "Default absolute threshold");
        for (double eps = 0x1.0p-53; eps <= 0.5; eps *= 2) {
            final double low = 1 - eps;
            final double high = 1 / low;
            Assertions.assertTrue(high - 1.0 >= eps);
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = {1e-6, 1e-10})
    void testGoldenRatioA(double eps) {
        final double actual = GeneralizedContinuedFraction.value(GoldenRatio.getInstance(), eps);
        Assertions.assertEquals(GOLDEN_RATIO, actual, GOLDEN_RATIO * eps);
    }





    @ParameterizedTest
    @ValueSource(doubles = {1e-6, 1e-10})
    void testGoldenRatioB(double eps) {
        final double actual = GeneralizedContinuedFraction.value(1, GoldenRatio.getInstance(), eps);
        Assertions.assertEquals(GOLDEN_RATIO, actual, GOLDEN_RATIO * eps);
    }





    @ParameterizedTest
    @ValueSource(doubles = {1e-6, 1e-10})
    void testGoldenRatioC(double eps) {
        final double actual = GeneralizedContinuedFraction.value(0, GoldenRatio.getInstance(), eps);
        Assertions.assertEquals(INV_GOLDEN_RATIO, actual, INV_GOLDEN_RATIO * eps);
    }





    @ParameterizedTest
    @ValueSource(doubles = {1e-6, 1e-10})
    void testBaseE(double eps) {
        final double actual = GeneralizedContinuedFraction.value(2, new E(), eps);
        Assertions.assertEquals(Math.E, actual, Math.E * eps);
    }





    @ParameterizedTest
    @CsvSource(value = {
        "0.5, 1e-6",
        "1.0, 1e-6",
        "2.0, 1e-6",
        "0.5, 1e-10",
        "1.0, 1e-10",
        "2.0, 1e-10",

        "1e-5, 1e-10",
        "1e-10, 1e-10",
        "1e-100, 1e-10",
        "1e-200, 1e-10",
    })
    void testTanX(double x, double eps) {
        final double expected = Math.tan(x);
        final double actual = GeneralizedContinuedFraction.value(0, new Tan(x), eps);
        Assertions.assertEquals(expected, actual, Math.abs(expected) * eps);
    }








    @ParameterizedTest
    @CsvSource(value = {



        "0.5, 0.5463024898437905132552",
        "0.125, 0.1256551365751309677927",
        "0.0078125, 0.007812658949600007638479",
    })
    void testTanX2(double x, double expected) {
        final double actual1 = GeneralizedContinuedFraction.value(0, new Tan(x));
        Assertions.assertNotEquals(expected, actual1, "Standard formulation");
        Assertions.assertEquals(expected, actual1, 2 * Math.ulp(expected));

        final Tan t = new Tan(x);
        final Coefficient c = t.get();
        final double actual2 = c.getA() / GeneralizedContinuedFraction.value(c.getB(), t);
        Assertions.assertEquals(expected, actual2, "Revised formulation");
    }







    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0x1.0p-100, 0, -1, Double.NaN})
    void testGoldenRatioAInvalidEpsilon(double eps) {
        final double actual = GeneralizedContinuedFraction.value(GoldenRatio.getInstance(), eps, 10000);


        Assertions.assertEquals(GOLDEN_RATIO, actual, Math.ulp(GOLDEN_RATIO));
    }







    @ParameterizedTest
    @ValueSource(doubles = {1.0, 0x1.0p-100, 0, -1, Double.NaN})
    void testGoldenRatioBInvalidEpsilon(double eps) {
        final double actual = GeneralizedContinuedFraction.value(1, GoldenRatio.getInstance(), eps, 10000);


        Assertions.assertEquals(GOLDEN_RATIO, actual, Math.ulp(GOLDEN_RATIO));
    }

    @Test
    void test415Over93() {










        final Supplier<Coefficient> gen = simpleContinuedFraction(4, 2, 6, 7);

        final double actual = GeneralizedContinuedFraction.value(gen);
        final double expected = 415.0 / 93;
        Assertions.assertEquals(expected, actual, Math.ulp(expected));
    }

    @Test
    void test43Over93() {



        final Supplier<Coefficient> gen = simpleContinuedFraction(2, 6, 7);


        final double actual = GeneralizedContinuedFraction.value(0, gen);
        final double expected = 43.0 / 93;
        Assertions.assertEquals(expected, actual, Math.ulp(expected));
    }

    @Test
    void test0Over93() {






        final Supplier<Coefficient> gen = simpleContinuedFraction(-43.0 / 93, 2, 6, 7);

        final double actual = GeneralizedContinuedFraction.value(gen);
        Assertions.assertEquals(0, actual, Math.ulp(0.5));
    }

    @Test
    void testMaxIterationsThrowsA() {
        final double eps = Math.ulp(1.0);


        Assertions.assertEquals(GOLDEN_RATIO,
            GeneralizedContinuedFraction.value(GoldenRatio.getInstance(), eps, 100), GOLDEN_RATIO * eps);

        final GoldenRatio gr = GoldenRatio.getInstance();
        final Throwable t = Assertions.assertThrows(ArithmeticException.class,
            () -> GeneralizedContinuedFraction.value(gr, eps, 3));
        assertExceptionMessageContains(t, "max");
    }

    @Test
    void testMaxIterationsThrowsB() {
        final double eps = Math.ulp(1.0);


        Assertions.assertEquals(INV_GOLDEN_RATIO,
            GeneralizedContinuedFraction.value(0, GoldenRatio.getInstance(), eps, 100), INV_GOLDEN_RATIO * eps);

        final GoldenRatio gr = GoldenRatio.getInstance();
        final Throwable t = Assertions.assertThrows(ArithmeticException.class,
            () -> GeneralizedContinuedFraction.value(0, gr, eps, 3));
        assertExceptionMessageContains(t, "max");
    }









    @Test
    void testDefaultIterationsBoundsNonConvergingFraction() {
        final int[] calls = {0};
        final Supplier<Coefficient> gen = () -> {
            calls[0]++;


            return Coefficient.of(1, calls[0] == 1 ? 1 : 0);
        };
        final Throwable t = Assertions.assertThrows(ArithmeticException.class,
            () -> GeneralizedContinuedFraction.value(gen));
        assertExceptionMessageContains(t, "max");

        Assertions.assertTrue(GeneralizedContinuedFraction.DEFAULT_ITERATIONS <= 1_000_000,
            "Default iterations should bound worst-case evaluation cost");
        Assertions.assertEquals(1 + GeneralizedContinuedFraction.DEFAULT_ITERATIONS, calls[0]);
    }

    @Test
    void testNaNThrowsA() {

        final Supplier<Coefficient> gen = simpleContinuedFraction(1, 1, Double.NaN);

        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(gen));
        assertExceptionMessageContains(t, "nan");
    }

    @Test
    void testNaNThrowsB() {

        final Supplier<Coefficient> gen = simpleContinuedFraction(1, 1, Double.NaN);

        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(0, gen));
        assertExceptionMessageContains(t, "nan");
    }














    @Test
    void testInfThrowsA() {



        final Supplier<Coefficient> gen = () -> Coefficient.of(Double.MAX_VALUE, 0.5);
        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(gen));
        assertExceptionMessageContains(t, "infinity");
    }














    @Test
    void testInfThrowsB() {




        final Supplier<Coefficient> gen = () -> Coefficient.of(Double.MAX_VALUE, 0.5);
        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(0, gen));
        assertExceptionMessageContains(t, "infinity");
    }














    @Test
    void testZeroThrowsA() {




        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {
            private int n;

            @Override
            public Coefficient get() {
                final double a = n++ < 2 ? 1 : Double.MAX_VALUE;
                return Coefficient.of(a, 0.5);
            }
        };
        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(gen, 1e-10, 100));



        assertExceptionMessageContains(t, "zero");
    }














    @Test
    void testZeroThrowsB() {




        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {
            private int n;

            @Override
            public Coefficient get() {
                final double a = n++ < 2 ? 1 : Double.MAX_VALUE;
                return Coefficient.of(a, 0.5);
            }
        };
        final Throwable t = Assertions.assertThrows(FractionException.class,
            () -> GeneralizedContinuedFraction.value(0, gen, 1e-10, 100));



        assertExceptionMessageContains(t, "zero");
    }

    private static void assertExceptionMessageContains(Throwable t, String text) {
        Assertions.assertTrue(t.getMessage().toLowerCase(Locale.ROOT).contains(text),
            () -> "Missing '" + text + "' from exception message: " + t.getMessage());
    }














    @Test
    void testCloseToZeroB0() {
        final double b0 = -1e-51;
        final double b1 = -1e49;

        final double expected = b0 + 1 / b1;

        Assertions.assertEquals(expected, GeneralizedContinuedFraction.value(b0, simpleContinuedFraction(b1)));

        Assertions.assertEquals(expected,
            GeneralizedContinuedFraction.value(simpleContinuedFraction(b0, b1)), Math.abs(expected) * 0.1);


        Assertions.assertEquals(-expected, GeneralizedContinuedFraction.value(-b0, simpleContinuedFraction(-b1)));
        Assertions.assertEquals(-expected,
            GeneralizedContinuedFraction.value(simpleContinuedFraction(-b0, -b1)), Math.abs(expected) * 0.1);
    }




    @ParameterizedTest
    @CsvSource(value = {

        "34.567, 35:-2:-3:-4:-3:-10",
        "-4.788, -5:5:-4:2:7",
        "67.492, 67:2:31:-4",
        "0.6782348, 1:-3:-9:-4:3:7:-19:3:-2:-11",
    })
    void testSimpleContinuedFraction(double x, String scf) {
        final double[] b = Arrays.stream(scf.split(":")).mapToDouble(Double::parseDouble).toArray();
        Assertions.assertEquals(x,
            GeneralizedContinuedFraction.value(simpleContinuedFraction(b)), Math.abs(x) * 1e-15);
    }






    @Test
    void testOneIteration() {
        final double eps = 0.5;
        final double gr = GeneralizedContinuedFraction.value(GoldenRatio.getInstance(), eps, 1);

        Assertions.assertEquals(2.0, gr);
    }






    @Test
    void testTwoIterations() {
        final double eps = 0.25;
        final double gr = GeneralizedContinuedFraction.value(GoldenRatio.getInstance(), eps, 2);

        Assertions.assertEquals(1.5, gr);
    }















    private static final class GoldenRatio implements Supplier<Coefficient> {
        private static final GoldenRatio INSTANCE = new GoldenRatio();
        private static final Coefficient RESULT = Coefficient.of(1, 1);




        static GoldenRatio getInstance() {
            return INSTANCE;
        }

        @Override
        public Coefficient get() {
            return RESULT;
        }
    }









    private static final class E implements Supplier<Coefficient> {
        private int n;

        @Override
        public Coefficient get() {
            n++;


            final double t = n % 3 == 2 ? 2 + 2 * (n / 3) : 1;
            return Coefficient.of(1, t);
        }
    }














    static final class Tan implements Supplier<Coefficient> {
        private double a;
        private double b = -1;




        Tan(double z) {
            a = z;
        }

        @Override
        public Coefficient get() {
            b += 2;

            if (b == 1) {
                final double z = a;



                a *= -z;
                return Coefficient.of(z, b);
            }
            return Coefficient.of(a, b);
        }
    }
















    static Supplier<Coefficient> simpleContinuedFraction(double... coefficients) {
        return new Supplier<Coefficient>() {

            private int n;

            private final double[] b = coefficients.clone();

            @Override
            public Coefficient get() {
                if (n != b.length) {

                    return Coefficient.of(1, b[n++]);
                }
                return Coefficient.of(0, 1);
            }
        };
    }
}
