
















package org.apache.commons.numbers.complex;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;








class CStandardTest {

    private static final double inf = Double.POSITIVE_INFINITY;
    private static final double negInf = Double.NEGATIVE_INFINITY;
    private static final double nan = Double.NaN;
    private static final double max = Double.MAX_VALUE;
    private static final double piOverFour = Math.PI / 4.0;
    private static final double piOverTwo = Math.PI / 2.0;
    private static final double threePiOverFour = 3.0 * Math.PI / 4.0;
    private static final Complex oneZero = complex(1, 0);
    private static final Complex zeroInf = complex(0, inf);
    private static final Complex zeroNegInf = complex(0, negInf);
    private static final Complex zeroNaN = complex(0, nan);
    private static final Complex zeroPiTwo = complex(0.0, piOverTwo);
    private static final Complex negZeroZero = complex(-0.0, 0);
    private static final Complex negZeroNaN = complex(-0.0, nan);
    private static final Complex infZero = complex(inf, 0);
    private static final Complex infNaN = complex(inf, nan);
    private static final Complex infInf = complex(inf, inf);
    private static final Complex infPiTwo = complex(inf, piOverTwo);
    private static final Complex infThreePiFour = complex(inf, threePiOverFour);
    private static final Complex infPiFour = complex(inf, piOverFour);
    private static final Complex infPi = complex(inf, Math.PI);
    private static final Complex negInfInf = complex(negInf, inf);
    private static final Complex negInfZero = complex(negInf, 0);
    private static final Complex negInfNaN = complex(negInf, nan);
    private static final Complex negInfPi = complex(negInf, Math.PI);
    private static final Complex nanInf = complex(nan, inf);
    private static final Complex nanNegInf = complex(nan, negInf);
    private static final Complex nanZero = complex(nan, 0);
    private static final Complex nanPiTwo = complex(nan, piOverTwo);
    private static final Complex piTwoNaN = complex(piOverTwo, nan);
    private static final Complex piNegInf = complex(Math.PI, negInf);
    private static final Complex piTwoNegInf = complex(piOverTwo, negInf);
    private static final Complex piTwoNegZero = complex(piOverTwo, -0.0);
    private static final Complex threePiFourNegInf = complex(threePiOverFour, negInf);
    private static final Complex piFourNegInf = complex(piOverFour, negInf);
    private static final Complex NAN = complex(nan, nan);
    private static final Complex maxMax = complex(max, max);
    private static final Complex maxNan = complex(max, nan);
    private static final Complex nanMax = complex(nan, max);


    private static final double[] finite;

    private static final double[] positiveFinite;

    private static final double[] nonZeroFinite;

    private static final double[] nonZeroPositiveFinite;

    private static final double[] nonZero;

    static {


        final int size = 13;
        finite = new double[size * 2];
        positiveFinite = new double[size];
        for (int i = 0; i < size; i++) {
            final double v = i * 0.5;
            finite[i * 2] = -v;
            finite[i * 2 + 1] = v;
            positiveFinite[i] = v;
        }


        nonZeroFinite = Arrays.copyOfRange(finite, 2, finite.length);
        nonZeroPositiveFinite = Arrays.copyOfRange(positiveFinite, 1, positiveFinite.length);

        nonZero = Arrays.copyOf(nonZeroFinite, nonZeroFinite.length + 2);
        nonZero[nonZeroFinite.length] = Double.POSITIVE_INFINITY;
        nonZero[nonZeroFinite.length + 1] = Double.NEGATIVE_INFINITY;


        Arrays.sort(finite);
        Arrays.sort(nonZeroFinite);
        Arrays.sort(nonZero);
    }




    private enum FunctionType {

        ODD,

        EVEN,

        NONE;
    }




    private enum UnspecifiedSign {

        REAL {
            @Override
            Complex removeSign(Complex c) {
                return negative(c.getReal()) ? complex(-c.getReal(), c.getImaginary()) : c;
            }
        },

        IMAGINARY {
            @Override
            Complex removeSign(Complex c) {
                return negative(c.getImaginary()) ? complex(c.getReal(), -c.getImaginary()) : c;
            }
        },

        REAL_IMAGINARY {
            @Override
            Complex removeSign(Complex c) {
                return IMAGINARY.removeSign(REAL.removeSign(c));
            }
        },

        NONE {
            @Override
            Complex removeSign(Complex c) {
                return c;
            }
        };







        abstract Complex removeSign(Complex c);













        private static boolean negative(double d) {
            return d < 0 || Double.doubleToLongBits(d) == Double.doubleToLongBits(-0.0);
        }
    }








    private static void assertComplex(Complex c1, Complex c2) {

        Assertions.assertEquals(c2.getReal(), c1.getReal(), 0.0, "real");
        Assertions.assertEquals(c2.getImaginary(), c1.getImaginary(), 0.0, "imaginary");
    }











    private static void assertOperation(Complex c1, Complex c2,
            BiFunction<Complex, Complex, Complex> operation, String operationName,
            Predicate<Complex> expected, String expectedName) {
        final Complex z = operation.apply(c1, c2);
        Assertions.assertTrue(expected.test(z),
            () -> String.format("%s expected: %s %s %s = %s", expectedName, c1, operationName, c2, z));
    }






























    private static void assertConjugateEquality(UnaryOperator<Complex> operation) {



        final double[] parts = {Double.NEGATIVE_INFINITY, -1, -0.0, 0.0, 1,
                                Double.POSITIVE_INFINITY, Double.NaN};
        for (final double x : parts) {
            for (final double y : parts) {

                if (!Double.isNaN(y)) {
                    assertConjugateEquality(complex(x, y), operation, UnspecifiedSign.NONE);
                }
            }
        }

        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 100; i++) {
            final double x = next(rng);
            final double y = next(rng);
            assertConjugateEquality(complex(x, y), operation, UnspecifiedSign.NONE);
        }
    }















    private static void assertConjugateEquality(Complex z,
            UnaryOperator<Complex> operation, UnspecifiedSign sign) {
        final Complex c1 = operation.apply(z.conj());
        final Complex c2 = operation.apply(z).conj();
        final Complex t1 = sign.removeSign(c1);
        final Complex t2 = sign.removeSign(c2);


        if (!equals(t1.getReal(), t2.getReal()) ||
            !equals(t1.getImaginary(), t2.getImaginary())) {
            Assertions.fail(
                String.format("Conjugate equality failed (z=%s). Expected: %s but was: %s (Unspecified sign = %s)",
                              z, c1, c2, sign));
        }
    }














    private static void assertFunctionType(UnaryOperator<Complex> operation, FunctionType type) {





        if (type == FunctionType.NONE) {
            return;
        }


        final double[] parts = {-2, -1, -0.0, 0.0, 1, 2};
        for (final double x : parts) {
            for (final double y : parts) {
                assertFunctionType(complex(x, y), operation, type, UnspecifiedSign.NONE);
            }
        }

        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 100; i++) {
            final double x = next(rng);
            final double y = next(rng);
            assertFunctionType(complex(x, y), operation, type, UnspecifiedSign.NONE);
        }
    }

















    private static void assertFunctionType(Complex z,
            UnaryOperator<Complex> operation, FunctionType type, UnspecifiedSign sign) {
        final Complex c1 = operation.apply(z);
        Complex c2 = operation.apply(z.negate());
        if (type == FunctionType.ODD) {
            c2 = c2.negate();
        }
        final Complex t1 = sign.removeSign(c1);
        final Complex t2 = sign.removeSign(c2);


        if (!equals(t1.getReal(), t2.getReal()) ||
            !equals(t1.getImaginary(), t2.getImaginary())) {
            Assertions.fail(
                String.format("%s equality failed (z=%s, -z=%s). Expected: %s but was: %s (Unspecified sign = %s)",
                              type, z, z.negate(), c1, c2, sign));
            new Exception().printStackTrace();
        }
    }















    private static void assertComplex(Complex z,
            UnaryOperator<Complex> operation, Complex expected) {
        assertComplex(z, operation, expected, FunctionType.NONE, UnspecifiedSign.NONE);
    }
















    private static void assertComplex(Complex z,
            UnaryOperator<Complex> operation, Complex expected, UnspecifiedSign sign) {
        assertComplex(z, operation, expected, FunctionType.NONE, sign);
    }























    private static void assertComplex(Complex z,
            UnaryOperator<Complex> operation, Complex expected,
            FunctionType type) {
        assertComplex(z, operation, expected, type, UnspecifiedSign.NONE);
    }




























    private static void assertComplex(Complex z,
            UnaryOperator<Complex> operation, Complex expected,
            FunctionType type, UnspecifiedSign sign) {






        final Complex c = operation.apply(z);
        final Complex t1 = sign.removeSign(c);
        final Complex t2 = sign.removeSign(expected);
        if (!equals(t1.getReal(), t2.getReal()) ||
            !equals(t1.getImaginary(), t2.getImaginary())) {
            Assertions.fail(
                String.format("Operation failed (z=%s). Expected: %s but was: %s (Unspecified sign = %s)",
                              z, expected, c, sign));
        }

        if (!Double.isNaN(z.getImaginary())) {
            assertConjugateEquality(z, operation, sign);
        }

        if (type != FunctionType.NONE) {
            assertFunctionType(z, operation, type, sign);








            if (!Double.isNaN(z.getImaginary())) {
                assertConjugateEquality(z.negate(), operation, sign);
            }
        }
    }

























    private static void assertAbs(Complex z, int ulps) {
        double x = z.getReal();
        double y = z.getImaginary();

        final double expected = Math.hypot(x, y);
        final double observed = z.abs();
        if (expected == observed) {

            return;
        }




        int scale = 0;
        x = Math.abs(x);
        y = Math.abs(y);
        if (Math.max(x, y) > 0x1.0p+500) {
            scale = Math.getExponent(Math.max(x, y));
        } else if (Math.min(x, y) < 0x1.0p-500) {
            scale = Math.getExponent(Math.min(x, y));
        }
        if (scale != 0) {
            x = Math.scalb(x, -scale);
            y = Math.scalb(y, -scale);
        }


        final double result = Math.sqrt(new BigDecimal(x).pow(2).add(new BigDecimal(y).pow(2)).doubleValue());
        final double exact = scale != 0 ? Math.scalb(result, scale) : result;
        if (exact == observed) {

            return;
        }

        final long obsBits = Double.doubleToLongBits(observed);
        final long exactBits = Double.doubleToLongBits(exact);
        final long obsUlp = Math.abs(exactBits - obsBits);
        Assertions.assertTrue(obsUlp <= ulps, () -> {

            final long expBits = Double.doubleToLongBits(expected);
            final long expUlp = Math.abs(exactBits - expBits);
            return String.format("%s.abs(). Expected %s, was %s (%d ulps). hypot %s (%d ulps)",
                z, exact, observed, obsUlp, expected, expUlp);
        });
    }












    private static void assertAbs(UniformRandomProvider rng,
                                  ToDoubleFunction<UniformRandomProvider> fx,
                                  ToDoubleFunction<UniformRandomProvider> fy,
                                  int samples) {
        for (int i = 0; i < samples; i++) {
            double x = fx.applyAsDouble(rng);
            double y = fy.applyAsDouble(rng);
            assertAbs(Complex.ofCartesian(x, y), 1);
        }
    }









    private static double createSubNormalNumber(UniformRandomProvider rng, int drop) {
        return Double.longBitsToDouble(rng.nextLong() >>> (12 + drop));
    }









    private static double createFixedExponentNumber(UniformRandomProvider rng, int exponent) {
        return Double.longBitsToDouble((rng.nextLong() >>> 12) | ((1023L + exponent) << 52));
    }









    private static boolean equals(double x, double y) {
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(y);
    }








    private static Complex complex(double real, double imaginary) {
        return Complex.ofCartesian(real, imaginary);
    }






    private static ArrayList<Complex> createInfinites() {
        final double[] values = {0, 1, inf, negInf, nan};
        return createCombinations(values, Complex::isInfinite);
    }






    private static ArrayList<Complex> createNonZeroFinites() {
        final double[] values = {-1, -0, 0, 1, Double.MAX_VALUE};
        return createCombinations(values, c -> !CStandardTest.isZero(c));
    }






    private static ArrayList<Complex> createZeroFinites() {
        final double[] values = {-0, 0};
        return createCombinations(values, c -> true);
    }






    private static ArrayList<Complex> createNaNs() {
        final double[] values = {0, 1, nan};
        return createCombinations(values, Complex::isNaN);
    }









    private static ArrayList<Complex> createCombinations(final double[] values, Predicate<Complex> condition) {
        final ArrayList<Complex> list = new ArrayList<>();
        for (final double re : values) {
            for (final double im : values) {
                final Complex z = complex(re, im);
                if (condition.test(z)) {
                    list.add(z);
                }
            }
        }
        return list;
    }








    private static boolean isZero(Complex c) {
        return c.getReal() == 0 && c.getImaginary() == 0;
    }




    @Test
    void testMultiply() {
        final ArrayList<Complex> infinites = createInfinites();
        final ArrayList<Complex> nonZeroFinites = createNonZeroFinites();
        final ArrayList<Complex> zeroFinites = createZeroFinites();



        Assertions.assertEquals(nan, 0.0 * inf, "0 * inf");
        Assertions.assertEquals(nan, 0.0 * negInf, "0 * -inf");
        Assertions.assertEquals(nan, -0.0 * inf, "-0 * inf");
        Assertions.assertEquals(nan, -0.0 * negInf, "-0 * -inf");



        for (final Complex z : infinites) {
            for (final Complex w : infinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isInfinite, "Inf");
                assertOperation(w, z, Complex::multiply, "*", Complex::isInfinite, "Inf");
            }
            for (final Complex w : nonZeroFinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isInfinite, "Inf");
                assertOperation(w, z, Complex::multiply, "*", Complex::isInfinite, "Inf");
            }


            for (final Complex w : zeroFinites) {
                assertOperation(z, w, Complex::multiply, "*", Complex::isNaN, "NaN");
                assertOperation(w, z, Complex::multiply, "*", Complex::isNaN, "NaN");
            }
        }











        final double[] values = {0, 1, inf, negInf, nan};
        for (final Complex z : createCombinations(values, c -> true)) {
            assertOperation(z, NAN, Complex::multiply, "*", Complex::isNaN, "NaN");
            assertOperation(NAN, z, Complex::multiply, "*", Complex::isNaN, "NaN");
        }


        assertOperation(maxMax, maxMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(maxNan, maxNan, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(nanMax, maxNan, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(maxNan, nanMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
        assertOperation(nanMax, nanMax, Complex::multiply, "*", Complex::isInfinite, "Inf");
    }




    @Test
    void testDivide() {
        final ArrayList<Complex> infinites = createInfinites();
        final ArrayList<Complex> nonZeroFinites = createNonZeroFinites();
        final ArrayList<Complex> zeroFinites = createZeroFinites();
        final ArrayList<Complex> nans = createNaNs();
        final ArrayList<Complex> finites = new ArrayList<>(nonZeroFinites);
        finites.addAll(zeroFinites);



        for (final Complex z : infinites) {
            for (final Complex w : nonZeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
            for (final Complex w : zeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }

            for (final Complex w : infinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isNaN, "NaN");
            }
        }



        for (final Complex z : finites) {
            for (final Complex w : infinites) {
                assertOperation(z, w, Complex::divide, "/", CStandardTest::isZero, "Zero");
            }
        }



        for (final Complex w : zeroFinites) {
            for (final Complex z : nonZeroFinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
            for (final Complex z : infinites) {
                assertOperation(z, w, Complex::divide, "/", Complex::isInfinite, "Inf");
            }
        }




        for (final Complex w : nans) {
            for (final Complex z : finites) {
                assertOperation(z, w, Complex::divide, "/", NAN::equals, "(NaN,NaN)");
            }
            for (final Complex z : infinites) {
                assertOperation(z, w, Complex::divide, "/", NAN::equals, "(NaN,NaN)");
            }
        }


        for (final Complex w : zeroFinites) {
            assertOperation(NAN, w, Complex::divide, "/", Complex::isNaN, "NaN");
        }
        for (final Complex w : infinites) {
            assertOperation(NAN, w, Complex::divide, "/", Complex::isNaN, "NaN");
        }
    }




    @Test
    void testSqrt1() {
        assertComplex(complex(-2.0, 0.0).sqrt(), complex(0.0, Math.sqrt(2)));
        assertComplex(complex(-2.0, -0.0).sqrt(), complex(0.0, -Math.sqrt(2)));
    }




    @Test
    void testImplicitTrig() {
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 100; i++) {
            final double re = next(rng);
            final double im = next(rng);
            final Complex z = complex(re, im);
            final Complex iz = z.multiplyImaginary(1);
            assertComplex(z.asin(), iz.asinh().multiplyImaginary(-1));
            assertComplex(z.atan(), iz.atanh().multiplyImaginary(-1));
            assertComplex(z.cos(), iz.cosh());
            assertComplex(z.sin(), iz.sinh().multiplyImaginary(-1));
            assertComplex(z.tan(), iz.tanh().multiplyImaginary(-1));
        }
    }







    private static double next(UniformRandomProvider rng) {

        return (rng.nextDouble() - rng.nextInt(1)) * 5;
    }





    @Test
    void testAbs() {
        Assertions.assertEquals(inf, complex(inf, nan).abs());
        Assertions.assertEquals(inf, complex(negInf, nan).abs());
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();
        for (int i = 0; i < 10; i++) {
            final double x = next(rng);
            final double y = next(rng);
            Assertions.assertEquals(complex(x, y).abs(), complex(y, x).abs());
            Assertions.assertEquals(complex(x, y).abs(), complex(x, -y).abs());
            Assertions.assertEquals(Math.abs(x), complex(x, 0.0).abs());
            Assertions.assertEquals(Math.abs(x), complex(x, -0.0).abs());
            Assertions.assertEquals(inf, complex(inf, y).abs());
            Assertions.assertEquals(inf, complex(negInf, y).abs());
        }



        final double[] parts = {-0.0, -Double.MIN_VALUE, -Double.MIN_NORMAL, -Double.MAX_VALUE,
            Double.NEGATIVE_INFINITY, Double.NaN};
        for (final double x : parts) {
            for (final double y : parts) {
                Assertions.assertEquals(Math.hypot(x, y), complex(x, y).abs());
            }
        }







        for (final double[] pair : new double[][] {
                {1.3122561682406755, 1.3122565442732959},
                {1.40905821964671, 1.4090583434236112},
                {1.912164268932753, 1.9121638616231227}}) {
            final Complex z = complex(pair[0], pair[1]);
            Assertions.assertEquals(z.abs(), z.multiplyImaginary(1).abs(), "Expected |z| == |iz|");
        }













        final int samples = 100;
        assertAbs(rng, r -> createSubNormalNumber(r, 0), r -> createSubNormalNumber(r, 0), samples);
        assertAbs(rng, r -> createSubNormalNumber(r, 0), r -> createSubNormalNumber(r, 1), samples);
        assertAbs(rng, r -> createSubNormalNumber(r, 0), r -> createSubNormalNumber(r, 2), samples);

        assertAbs(rng, r -> createFixedExponentNumber(r, 0), r -> createFixedExponentNumber(r, 0), samples);

        assertAbs(rng, r -> createFixedExponentNumber(r, 0), r -> createFixedExponentNumber(r, 1), samples);
        assertAbs(rng, r -> createFixedExponentNumber(r, 0), r -> createFixedExponentNumber(r, 2 + r.nextInt(10)), samples);

        assertAbs(rng, r -> createFixedExponentNumber(r, 1022), r -> createFixedExponentNumber(r, 1022), samples);
        assertAbs(rng, r -> createFixedExponentNumber(r, -1022), r -> createFixedExponentNumber(r, -1022), samples);

        final ToDoubleFunction<UniformRandomProvider> cisGenerator = new ToDoubleFunction<UniformRandomProvider>() {
            private double tmp = Double.NaN;
            @Override
            public double applyAsDouble(UniformRandomProvider rng) {
                if (Double.isNaN(tmp)) {
                    double u = rng.nextDouble() * Math.PI;
                    tmp = Math.cos(u);
                    return Math.sin(u);
                }
                final double r = tmp;
                tmp = Double.NaN;
                return r;
            }
        };
        assertAbs(rng, cisGenerator, cisGenerator, samples);
    }




    @Test
    void testAcos() {
        final UnaryOperator<Complex> operation = Complex::acos;
        assertConjugateEquality(operation);
        assertComplex(Complex.ZERO, operation, piTwoNegZero);
        assertComplex(negZeroZero, operation, piTwoNegZero);
        assertComplex(zeroNaN, operation, piTwoNaN);
        assertComplex(negZeroNaN, operation, piTwoNaN);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, piTwoNegInf);
        }
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(-inf, y), operation, piNegInf);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, zeroNegInf);
        }
        assertComplex(negInfInf, operation, threePiFourNegInf);
        assertComplex(infInf, operation, piFourNegInf);
        assertComplex(infNaN, operation, nanInf, UnspecifiedSign.IMAGINARY);
        assertComplex(negInfNaN, operation, nanNegInf, UnspecifiedSign.IMAGINARY);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(nanInf, operation, nanNegInf);
        assertComplex(NAN, operation, NAN);
    }







    @Test
    void testAcosh() {
        final UnaryOperator<Complex> operation = Complex::acosh;
        assertConjugateEquality(operation);
        assertComplex(Complex.ZERO, operation, zeroPiTwo);
        assertComplex(negZeroZero, operation, zeroPiTwo);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, infPiTwo);
        }
        assertComplex(zeroNaN, operation, nanPiTwo);
        assertComplex(negZeroNaN, operation, nanPiTwo);
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(-inf, y), operation, infPi);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, infZero);
        }
        assertComplex(negInfInf, operation, infThreePiFour);
        assertComplex(infInf, operation, infPiFour);
        assertComplex(infNaN, operation, infNaN);
        assertComplex(negInfNaN, operation, infNaN);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(nanInf, operation, infNaN);
        assertComplex(NAN, operation, NAN);
    }




    @Test
    void testAsinh() {
        final UnaryOperator<Complex> operation = Complex::asinh;
        final FunctionType type = FunctionType.ODD;
        assertConjugateEquality(operation);
        assertFunctionType(operation, type);
        assertComplex(Complex.ZERO, operation, Complex.ZERO, type);
        for (double x : positiveFinite) {
            assertComplex(complex(x, inf), operation, infPiTwo, type);
        }
        for (double x : finite) {
            assertComplex(complex(x, nan), operation, NAN, type);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, infZero, type);
        }
        assertComplex(infInf, operation, infPiFour, type);
        assertComplex(infNaN, operation, infNaN, type);
        assertComplex(nanZero, operation, nanZero, type);
        for (double y : nonZeroFinite) {
            assertComplex(complex(nan, y), operation, NAN, type);
        }
        assertComplex(nanInf, operation, infNaN, type, UnspecifiedSign.REAL);
        assertComplex(NAN, operation, NAN, type);
    }




    @Test
    void testAtanh() {
        final UnaryOperator<Complex> operation = Complex::atanh;
        final FunctionType type = FunctionType.ODD;
        assertConjugateEquality(operation);
        assertFunctionType(operation, type);
        assertComplex(Complex.ZERO, operation, Complex.ZERO, type);
        assertComplex(zeroNaN, operation, zeroNaN, type);
        assertComplex(oneZero, operation, infZero, type);
        for (double x : positiveFinite) {
            assertComplex(complex(x, inf), operation, zeroPiTwo, type);
        }
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN, type);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, zeroPiTwo, type);
        }
        assertComplex(infInf, operation, zeroPiTwo, type);
        assertComplex(infNaN, operation, zeroNaN, type);
        assertComplex(nanZero, operation, NAN, type);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN, type);
        }
        assertComplex(nanInf, operation, zeroPiTwo, type, UnspecifiedSign.REAL);
        assertComplex(NAN, operation, NAN, type);
    }




    @Test
    void testCosh() {
        final UnaryOperator<Complex> operation = Complex::cosh;
        final FunctionType type = FunctionType.EVEN;
        assertConjugateEquality(operation);
        assertFunctionType(operation, type);
        assertComplex(Complex.ZERO, operation, Complex.ONE, type);
        assertComplex(zeroInf, operation, nanZero, type, UnspecifiedSign.IMAGINARY);
        assertComplex(zeroNaN, operation, nanZero, type, UnspecifiedSign.IMAGINARY);
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, inf), operation, NAN, type);
        }
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN, type);
        }
        assertComplex(infZero, operation, infZero, type);
        for (double y : nonZeroFinite) {
            assertComplex(complex(inf, y), operation, Complex.ofCis(y).multiply(inf), type);
        }
        assertComplex(infInf, operation, infNaN, type, UnspecifiedSign.REAL);
        assertComplex(infNaN, operation, infNaN, type);
        assertComplex(nanZero, operation, nanZero, type, UnspecifiedSign.IMAGINARY);
        for (double y : nonZero) {
            assertComplex(complex(nan, y), operation, NAN, type);
        }
        assertComplex(NAN, operation, NAN, type);
    }




    @Test
    void testSinh() {
        final UnaryOperator<Complex> operation = Complex::sinh;
        final FunctionType type = FunctionType.ODD;
        assertConjugateEquality(operation);
        assertFunctionType(operation, type);
        assertComplex(Complex.ZERO, operation, Complex.ZERO, type);
        assertComplex(zeroInf, operation, zeroNaN, type, UnspecifiedSign.REAL);
        assertComplex(zeroNaN, operation, zeroNaN, type, UnspecifiedSign.REAL);
        for (double x : nonZeroPositiveFinite) {
            assertComplex(complex(x, inf), operation, NAN, type);
        }
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN, type);
        }
        assertComplex(infZero, operation, infZero, type);

        for (double y : nonZeroFinite) {
            assertComplex(complex(inf, y), operation, Complex.ofCis(y).multiply(inf), type);
        }
        assertComplex(infInf, operation, infNaN, type, UnspecifiedSign.REAL);
        assertComplex(infNaN, operation, infNaN, type, UnspecifiedSign.REAL);
        assertComplex(nanZero, operation, nanZero, type);
        for (double y : nonZero) {
            assertComplex(complex(nan, y), operation, NAN, type);
        }
        assertComplex(NAN, operation, NAN, type);
    }







    @Test
    void testTanh() {
        final UnaryOperator<Complex> operation = Complex::tanh;
        final FunctionType type = FunctionType.ODD;
        assertConjugateEquality(operation);
        assertFunctionType(operation, type);
        assertComplex(Complex.ZERO, operation, Complex.ZERO, type);
        assertComplex(zeroInf, operation, zeroNaN, type);
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, inf), operation, NAN, type);
        }
        assertComplex(zeroNaN, operation, zeroNaN, type);
        for (double x : nonZeroFinite) {
            assertComplex(complex(x, nan), operation, NAN, type);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, complex(1.0, Math.copySign(0, Math.sin(2 * y))), type);
        }
        assertComplex(infInf, operation, oneZero, type, UnspecifiedSign.IMAGINARY);
        assertComplex(infNaN, operation, oneZero, type, UnspecifiedSign.IMAGINARY);
        assertComplex(nanZero, operation, nanZero, type);
        for (double y : nonZero) {
            assertComplex(complex(nan, y), operation, NAN, type);
        }
        assertComplex(NAN, operation, NAN, type);
    }




    @Test
    void testExp() {
        final UnaryOperator<Complex> operation = Complex::exp;
        assertConjugateEquality(operation);
        assertComplex(Complex.ZERO, operation, oneZero);
        assertComplex(negZeroZero, operation, oneZero);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, NAN);
        }
        for (double x : finite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        assertComplex(infZero, operation, infZero);
        for (double y : finite) {
            assertComplex(complex(-inf, y), operation, Complex.ofCis(y).multiply(0.0));
        }
        for (double y : nonZeroFinite) {
            assertComplex(complex(inf, y), operation, Complex.ofCis(y).multiply(inf));
        }
        assertComplex(negInfInf, operation, Complex.ZERO, UnspecifiedSign.REAL_IMAGINARY);
        assertComplex(infInf, operation, infNaN, UnspecifiedSign.REAL);
        assertComplex(negInfNaN, operation, Complex.ZERO, UnspecifiedSign.REAL_IMAGINARY);
        assertComplex(infNaN, operation, infNaN, UnspecifiedSign.REAL);
        assertComplex(nanZero, operation, nanZero);
        for (double y : nonZero) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(NAN, operation, NAN);
    }




    @Test
    void testLog() {
        final UnaryOperator<Complex> operation = Complex::log;
        assertConjugateEquality(operation);
        assertComplex(negZeroZero, operation, negInfPi);
        assertComplex(Complex.ZERO, operation, negInfZero);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, infPiTwo);
        }
        for (double x : finite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(-inf, y), operation, infPi);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, infZero);
        }
        assertComplex(negInfInf, operation, infThreePiFour);
        assertComplex(infInf, operation, infPiFour);
        assertComplex(negInfNaN, operation, infNaN);
        assertComplex(infNaN, operation, infNaN);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(nanInf, operation, infNaN);
        assertComplex(NAN, operation, NAN);
    }





    @Test
    void testLog10() {
        final UnaryOperator<Complex> operation = Complex::log10;
        assertConjugateEquality(operation);
        assertComplex(negZeroZero, operation, negInfPi);
        assertComplex(Complex.ZERO, operation, negInfZero);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, infPiTwo);
        }
        for (double x : finite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(-inf, y), operation, infPi);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, infZero);
        }
        assertComplex(negInfInf, operation, infThreePiFour);
        assertComplex(infInf, operation, infPiFour);
        assertComplex(negInfNaN, operation, infNaN);
        assertComplex(infNaN, operation, infNaN);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(nanInf, operation, infNaN);
        assertComplex(NAN, operation, NAN);
    }




    @Test
    void testSqrt() {
        final UnaryOperator<Complex> operation = Complex::sqrt;
        assertConjugateEquality(operation);
        assertComplex(negZeroZero, operation, Complex.ZERO);
        assertComplex(Complex.ZERO, operation, Complex.ZERO);
        for (double x : finite) {
            assertComplex(complex(x, inf), operation, infInf);
        }

        assertComplex(infInf, operation, infInf);
        assertComplex(negInfInf, operation, infInf);
        assertComplex(nanInf, operation, infInf);
        for (double x : finite) {
            assertComplex(complex(x, nan), operation, NAN);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(-inf, y), operation, zeroInf);
        }
        for (double y : positiveFinite) {
            assertComplex(complex(inf, y), operation, infZero);
        }
        assertComplex(negInfNaN, operation, nanInf, UnspecifiedSign.IMAGINARY);
        assertComplex(infNaN, operation, infNaN);
        for (double y : finite) {
            assertComplex(complex(nan, y), operation, NAN);
        }
        assertComplex(NAN, operation, NAN);
    }
}
