
















package org.apache.commons.numbers.complex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.DoubleFunction;
import java.util.function.Supplier;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;














class ComplexTest {

    private static final double inf = Double.POSITIVE_INFINITY;
    private static final double neginf = Double.NEGATIVE_INFINITY;
    private static final double nan = Double.NaN;
    private static final double pi = Math.PI;
    private static final Complex oneInf = Complex.ofCartesian(1, inf);
    private static final Complex oneNegInf = Complex.ofCartesian(1, neginf);
    private static final Complex infOne = Complex.ofCartesian(inf, 1);
    private static final Complex infZero = Complex.ofCartesian(inf, 0);
    private static final Complex infNegZero = Complex.ofCartesian(inf, -0.0);
    private static final Complex infNegInf = Complex.ofCartesian(inf, neginf);
    private static final Complex infInf = Complex.ofCartesian(inf, inf);
    private static final Complex negInfInf = Complex.ofCartesian(neginf, inf);
    private static final Complex negInfOne = Complex.ofCartesian(neginf, 1);
    private static final Complex negInfNegInf = Complex.ofCartesian(neginf, neginf);
    private static final Complex oneNan = Complex.ofCartesian(1, nan);
    private static final Complex zeroInf = Complex.ofCartesian(0, inf);
    private static final Complex zeroNan = Complex.ofCartesian(0, nan);
    private static final Complex nanZero = Complex.ofCartesian(nan, 0);
    private static final Complex NAN = Complex.ofCartesian(nan, nan);
    private static final Complex INF = Complex.ofCartesian(inf, inf);




    private enum NumberType {
        NAN, INFINITE, FINITE
    }







    private static Complex ofReal(double real) {
        return Complex.ofCartesian(real, 0);
    }







    private static Complex ofImaginary(double imaginary) {
        return Complex.ofCartesian(0, imaginary);
    }

    @Test
    @Disabled("Used to output the java environment")
    @SuppressWarnings("squid:S2699")
    void testJava() {

        System.out.println(">>testJava()");
        final String[] props = {"java.version",
            "java.vendor",
            "java.vm.specification.version",
            "java.vm.specification.vendor",
            "java.vm.specification.name",
            "java.vm.version",
            "java.vm.vendor",
            "java.vm.name",
            "java.specification.version",

            "java.specification.vendor",
            "java.specification.name",
            "java.class.version",
        };
        for (final String t : props) {
            System.out.println(t + "=" + System.getProperty(t));
        }
        System.out.println("<<testJava()");

    }

    @Test
    void testCartesianConstructor() {
        final Complex z = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());
    }

    @Test
    void testPolarConstructor() {
        final double r = 98765;
        final double theta = 0.12345;
        final Complex z = Complex.ofPolar(r, theta);
        final Complex y = Complex.ofCis(theta);
        Assertions.assertEquals(r * y.getReal(), z.getReal());
        Assertions.assertEquals(r * y.getImaginary(), z.getImaginary());



        Assertions.assertEquals(NAN, Complex.ofPolar(1, -inf));
        Assertions.assertEquals(NAN, Complex.ofPolar(1, inf));
        Assertions.assertEquals(NAN, Complex.ofPolar(1, nan));


        Assertions.assertEquals(NAN, Complex.ofPolar(inf, nan));

        Assertions.assertEquals(NAN, Complex.ofPolar(-inf, 1));
        Assertions.assertEquals(NAN, Complex.ofPolar(-0.0, 1));
        Assertions.assertEquals(NAN, Complex.ofPolar(nan, 1));



        Assertions.assertEquals(NAN, Complex.ofPolar(-0.0, 0.0));
        Assertions.assertEquals(Complex.ofCartesian(0.0, 0.0), Complex.ofPolar(0.0, 0.0));
        Assertions.assertEquals(Complex.ofCartesian(1.0, 0.0), Complex.ofPolar(1.0, 0.0));
        Assertions.assertEquals(Complex.ofCartesian(-1.0, Math.sin(pi)), Complex.ofPolar(1.0, pi));
        Assertions.assertEquals(Complex.ofCartesian(-inf, inf), Complex.ofPolar(inf, pi));
        Assertions.assertEquals(Complex.ofCartesian(inf, nan), Complex.ofPolar(inf, 0.0));
        Assertions.assertEquals(Complex.ofCartesian(inf, -inf), Complex.ofPolar(inf, -pi / 4));
        Assertions.assertEquals(Complex.ofCartesian(-inf, -inf), Complex.ofPolar(inf, 5 * pi / 4));
    }

    @Test
    void testPolarConstructorAbsArg() {


        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create(678678638L);
        for (int i = 0; i < 10; i++) {
            final double rho = rng.nextDouble();

            final double theta = pi - rng.nextDouble() * 2 * pi;
            final Complex z = Complex.ofPolar(rho, theta);

            Assertions.assertEquals(rho, z.abs(), Math.ulp(rho));
            Assertions.assertEquals(theta, z.arg(), Math.ulp(theta));
        }
    }

    @Test
    void testCisConstructor() {
        final double x = 0.12345;
        final Complex z = Complex.ofCis(x);
        Assertions.assertEquals(Math.cos(x), z.getReal());
        Assertions.assertEquals(Math.sin(x), z.getImaginary());
    }




    @Test
    void testParseAndToString() {
        final double[] parts = {Double.NEGATIVE_INFINITY, -1, -0.0, 0.0, 1, Math.PI, Double.POSITIVE_INFINITY,
            Double.NaN};
        for (final double x : parts) {
            for (final double y : parts) {
                final Complex z = Complex.ofCartesian(x, y);
                Assertions.assertEquals(z, Complex.parse(z.toString()));
            }
        }
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 10; i++) {
            final double x = -1 + rng.nextDouble() * 2;
            final double y = -1 + rng.nextDouble() * 2;
            final Complex z = Complex.ofCartesian(x, y);
            Assertions.assertEquals(z, Complex.parse(z.toString()));
        }


        Assertions.assertEquals(Complex.ofPolar(2, pi), Complex.parse(Complex.ofPolar(2, pi).toString()));
        Assertions.assertEquals(Complex.ofCis(pi), Complex.parse(Complex.ofCis(pi).toString()));
    }

    @Test
    void testParseNull() {
        Assertions.assertThrows(NullPointerException.class, () -> Complex.parse(null));
    }

    @Test
    void testParseEmpty() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse(""));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse(" "));
    }

    @Test
    void testParseWrongStart() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("1.0,2.0)"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("[1.0,2.0)"));
    }

    @Test
    void testParseWrongEnd() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2.0"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2.0]"));
    }

    @Test
    void testParseWrongSeparator() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0 2.0)"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0:2.0)"));
    }

    @Test
    void testParseSeparatorOutsideStartAndEnd() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2.0),"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse(",(1.0,2.0)"));
    }

    @Test
    void testParseExtraSeparator() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,,2.0)"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2.0,)"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(,1.0,2.0)"));
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2,0)"));
    }

    @Test
    void testParseInvalidRe() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(I.0,2.0)"));
    }

    @Test
    void testParseInvalidIm() {
        Assertions.assertThrows(NumberFormatException.class, () -> Complex.parse("(1.0,2.G)"));
    }

    @Test
    void testParseSpaceAllowedAroundNumbers() {
        final double re = 1.234;
        final double im = 5.678;
        final Complex z = Complex.ofCartesian(re, im);
        Assertions.assertEquals(z, Complex.parse("(" + re + "," + im + ")"));
        Assertions.assertEquals(z, Complex.parse("( " + re + "," + im + ")"));
        Assertions.assertEquals(z, Complex.parse("(" + re + " ," + im + ")"));
        Assertions.assertEquals(z, Complex.parse("(" + re + ", " + im + ")"));
        Assertions.assertEquals(z, Complex.parse("(" + re + "," + im + " )"));
        Assertions.assertEquals(z, Complex.parse("(  " + re + "  , " + im + "     )"));
    }

    @Test
    void testCGrammar() {
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 10; i++) {
            final Complex z = Complex.ofCartesian(rng.nextDouble(), rng.nextDouble());
            Assertions.assertEquals(z.getReal(), z.real(), "real");
            Assertions.assertEquals(z.getImaginary(), z.imag(), "imag");
        }
    }

    @Test
    void testAbs() {
        final Complex z = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertEquals(5.0, z.abs());
    }

    @Test
    void testAbsNaN() {

        Assertions.assertEquals(nan, NAN.abs());
        Assertions.assertEquals(nan, Complex.ofCartesian(3.0, nan).abs());
        Assertions.assertEquals(nan, Complex.ofCartesian(nan, 3.0).abs());

        Assertions.assertEquals(inf, Complex.ofCartesian(inf, nan).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(-inf, nan).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(nan, inf).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(nan, -inf).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(inf, 3.0).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(-inf, 3.0).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(3.0, inf).abs());
        Assertions.assertEquals(inf, Complex.ofCartesian(3.0, -inf).abs());
    }




    @Test
    void testArg() {
        Complex z = Complex.ofCartesian(1, 0);
        assertArgument(0.0, z, 1.0e-12);

        z = Complex.ofCartesian(1, 1);
        assertArgument(Math.PI / 4, z, 1.0e-12);

        z = Complex.ofCartesian(0, 1);
        assertArgument(Math.PI / 2, z, 1.0e-12);

        z = Complex.ofCartesian(-1, 1);
        assertArgument(3 * Math.PI / 4, z, 1.0e-12);

        z = Complex.ofCartesian(-1, 0);
        assertArgument(Math.PI, z, 1.0e-12);

        z = Complex.ofCartesian(-1, -1);
        assertArgument(-3 * Math.PI / 4, z, 1.0e-12);

        z = Complex.ofCartesian(0, -1);
        assertArgument(-Math.PI / 2, z, 1.0e-12);

        z = Complex.ofCartesian(1, -1);
        assertArgument(-Math.PI / 4, z, 1.0e-12);
    }




    @Test
    void testArgInf() {
        assertArgument(Math.PI / 4, infInf, 1.0e-12);
        assertArgument(Math.PI / 2, oneInf, 1.0e-12);
        assertArgument(0.0, infOne, 1.0e-12);
        assertArgument(Math.PI / 2, zeroInf, 1.0e-12);
        assertArgument(0.0, infZero, 1.0e-12);
        assertArgument(Math.PI, negInfOne, 1.0e-12);
        assertArgument(-3.0 * Math.PI / 4, negInfNegInf, 1.0e-12);
        assertArgument(-Math.PI / 2, oneNegInf, 1.0e-12);
    }




    @Test
    void testArgNaN() {
        assertArgument(Double.NaN, nanZero, 0);
        assertArgument(Double.NaN, zeroNan, 0);
        assertArgument(Double.NaN, NAN, 0);
    }

    private static void assertArgument(double expected, Complex complex, double delta) {
        final double actual = complex.arg();
        Assertions.assertEquals(expected, actual, delta);
        Assertions.assertEquals(actual, complex.arg(), delta);
    }

    @Test
    void testNorm() {
        final Complex z = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertEquals(25.0, z.norm());
    }

    @Test
    void testNormNaN() {

        Assertions.assertEquals(nan, NAN.norm());
        Assertions.assertEquals(nan, Complex.ofCartesian(3.0, nan).norm());
        Assertions.assertEquals(nan, Complex.ofCartesian(nan, 3.0).norm());

        Assertions.assertEquals(inf, Complex.ofCartesian(inf, nan).norm());
        Assertions.assertEquals(inf, Complex.ofCartesian(-inf, nan).norm());
        Assertions.assertEquals(inf, Complex.ofCartesian(nan, inf).norm());
        Assertions.assertEquals(inf, Complex.ofCartesian(nan, -inf).norm());
    }




    @Test
    void testNumberType() {
        assertNumberType(0, 0, NumberType.FINITE);
        assertNumberType(1, 0, NumberType.FINITE);
        assertNumberType(0, 1, NumberType.FINITE);

        assertNumberType(inf, 0, NumberType.INFINITE);
        assertNumberType(-inf, 0, NumberType.INFINITE);
        assertNumberType(0, inf, NumberType.INFINITE);
        assertNumberType(0, -inf, NumberType.INFINITE);



        assertNumberType(inf, nan, NumberType.INFINITE);
        assertNumberType(-inf, nan, NumberType.INFINITE);
        assertNumberType(nan, inf, NumberType.INFINITE);
        assertNumberType(nan, -inf, NumberType.INFINITE);

        assertNumberType(nan, 0, NumberType.NAN);
        assertNumberType(0, nan, NumberType.NAN);
        assertNumberType(nan, nan, NumberType.NAN);
    }









    private static void assertNumberType(double real, double imaginary, NumberType type) {
        final Complex z = Complex.ofCartesian(real, imaginary);
        final boolean isNaN = z.isNaN();
        final boolean isInfinite = z.isInfinite();
        final boolean isFinite = z.isFinite();

        int count = isNaN ? 1 : 0;
        count += isInfinite ? 1 : 0;
        count += isFinite ? 1 : 0;
        Assertions.assertEquals(1, count,
            () -> String.format("Complex can be only one type: isNaN=%s, isInfinite=%s, isFinite=%s: %s", isNaN,
                isInfinite, isFinite, z));
        switch (type) {
        case FINITE:
            Assertions.assertTrue(isFinite, () -> "not finite: " + z);
            break;
        case INFINITE:
            Assertions.assertTrue(isInfinite, () -> "not infinite: " + z);
            break;
        case NAN:
            Assertions.assertTrue(isNaN, () -> "not nan: " + z);
            break;
        default:
            Assertions.fail("Unknown number type");
        }
    }

    @Test
    void testConjugate() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex z = x.conj();
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(-4.0, z.getImaginary());
    }

    @Test
    void testConjugateNaN() {
        final Complex z = NAN.conj();
        Assertions.assertTrue(z.isNaN());
    }

    @Test
    void testConjugateInfinite() {
        Complex z = Complex.ofCartesian(0, inf);
        Assertions.assertEquals(neginf, z.conj().getImaginary());
        z = Complex.ofCartesian(0, neginf);
        Assertions.assertEquals(inf, z.conj().getImaginary());
    }

    @Test
    void testNegate() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex z = x.negate();
        Assertions.assertEquals(-3.0, z.getReal());
        Assertions.assertEquals(-4.0, z.getImaginary());
    }

    @Test
    void testNegateNaN() {
        final Complex z = NAN.negate();
        Assertions.assertTrue(z.isNaN());
    }

    @Test
    void testProj() {
        final Complex z = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertSame(z, z.proj());

        TestUtils.assertSame(infZero, Complex.ofCartesian(inf, 4.0).proj());
        TestUtils.assertSame(infZero, Complex.ofCartesian(inf, inf).proj());
        TestUtils.assertSame(infZero, Complex.ofCartesian(inf, nan).proj());
        TestUtils.assertSame(infZero, Complex.ofCartesian(3.0, inf).proj());
        TestUtils.assertSame(infZero, Complex.ofCartesian(nan, inf).proj());
        TestUtils.assertSame(infNegZero, Complex.ofCartesian(inf, -4.0).proj());
        TestUtils.assertSame(infNegZero, Complex.ofCartesian(inf, -inf).proj());
        TestUtils.assertSame(infNegZero, Complex.ofCartesian(3.0, -inf).proj());
        TestUtils.assertSame(infNegZero, Complex.ofCartesian(nan, -inf).proj());
    }

    @Test
    void testAdd() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(5.0, 6.0);
        final Complex z = x.add(y);
        Assertions.assertEquals(8.0, z.getReal());
        Assertions.assertEquals(10.0, z.getImaginary());
    }

    @Test
    void testAddInf() {
        Complex x = Complex.ofCartesian(1, 1);
        final Complex z = Complex.ofCartesian(inf, 0);
        final Complex w = x.add(z);
        Assertions.assertEquals(1, w.getImaginary());
        Assertions.assertEquals(inf, w.getReal());

        x = Complex.ofCartesian(neginf, 0);
        Assertions.assertTrue(Double.isNaN(x.add(z).getReal()));
    }

    @Test
    void testAddReal() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.add(y);
        Assertions.assertEquals(8.0, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofReal(y)));
    }

    @Test
    void testAddRealNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.add(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofReal(y)));
    }

    @Test
    void testAddRealInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.add(y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofReal(y)));
    }

    @Test
    void testAddRealWithNegZeroImaginary() {
        final Complex x = Complex.ofCartesian(3.0, -0.0);
        final double y = 5.0;
        final Complex z = x.add(y);
        Assertions.assertEquals(8.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary(), "Expected sign preservation");

        final Complex z2 = x.add(ofReal(y));
        Assertions.assertEquals(8.0, z2.getReal());
        Assertions.assertEquals(0.0, z2.getImaginary(), "Expected no-sign preservation");
    }

    @Test
    void testAddImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.addImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(9.0, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofImaginary(y)));
    }

    @Test
    void testAddImaginaryNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.addImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofImaginary(y)));
    }

    @Test
    void testAddImaginaryInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.addImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        Assertions.assertEquals(z, x.add(ofImaginary(y)));
    }

    @Test
    void testAddImaginaryWithNegZeroReal() {
        final Complex x = Complex.ofCartesian(-0.0, 4.0);
        final double y = 5.0;
        final Complex z = x.addImaginary(y);
        Assertions.assertEquals(-0.0, z.getReal(), "Expected sign preservation");
        Assertions.assertEquals(9.0, z.getImaginary());

        final Complex z2 = x.add(ofImaginary(y));
        Assertions.assertEquals(0.0, z2.getReal(), "Expected no-sign preservation");
        Assertions.assertEquals(9.0, z2.getImaginary());
    }

    @Test
    void testSubtract() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(5.0, 7.0);
        final Complex z = x.subtract(y);
        Assertions.assertEquals(-2.0, z.getReal());
        Assertions.assertEquals(-3.0, z.getImaginary());
    }

    @Test
    void testSubtractInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(inf, 7.0);
        Complex z = x.subtract(y);
        Assertions.assertEquals(neginf, z.getReal());
        Assertions.assertEquals(-3.0, z.getImaginary());

        z = y.subtract(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(0.0, z.getImaginary());
    }

    @Test
    void testSubtractReal() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtract(y);
        Assertions.assertEquals(-2.0, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofReal(y)));
    }

    @Test
    void testSubtractRealNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.subtract(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofReal(y)));
    }

    @Test
    void testSubtractRealInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.subtract(y);
        Assertions.assertEquals(-inf, z.getReal());
        Assertions.assertEquals(4.0, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofReal(y)));
    }

    @Test
    void testSubtractRealWithNegZeroImaginary() {
        final Complex x = Complex.ofCartesian(3.0, -0.0);
        final double y = 5.0;
        final Complex z = x.subtract(y);
        Assertions.assertEquals(-2.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary());


        Assertions.assertEquals(z, x.subtract(ofReal(y)));
    }

    @Test
    void testSubtractImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtractImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(-1.0, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofImaginary(y)));
    }

    @Test
    void testSubtractImaginaryNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.subtractImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofImaginary(y)));
    }

    @Test
    void testSubtractImaginaryInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.subtractImaginary(y);
        Assertions.assertEquals(3.0, z.getReal());
        Assertions.assertEquals(-inf, z.getImaginary());

        Assertions.assertEquals(z, x.subtract(ofImaginary(y)));
    }

    @Test
    void testSubtractImaginaryWithNegZeroReal() {
        final Complex x = Complex.ofCartesian(-0.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtractImaginary(y);
        Assertions.assertEquals(-0.0, z.getReal());
        Assertions.assertEquals(-1.0, z.getImaginary());


        Assertions.assertEquals(z, x.subtract(ofImaginary(y)));
    }

    @Test
    void testSubtractFromReal() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtractFrom(y);
        Assertions.assertEquals(2.0, z.getReal());
        Assertions.assertEquals(-4.0, z.getImaginary());

        Assertions.assertEquals(z, ofReal(y).subtract(x));
    }

    @Test
    void testSubtractFromRealNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.subtractFrom(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(-4.0, z.getImaginary());

        Assertions.assertEquals(z, ofReal(y).subtract(x));
    }

    @Test
    void testSubtractFromRealInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.subtractFrom(y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(-4.0, z.getImaginary());

        Assertions.assertEquals(z, ofReal(y).subtract(x));
    }

    @Test
    void testSubtractFromRealWithPosZeroImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 0.0);
        final double y = 5.0;
        final Complex z = x.subtractFrom(y);
        Assertions.assertEquals(2.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary(), "Expected sign inversion");

        Assertions.assertNotEquals(z, ofReal(y).subtract(x));
    }

    @Test
    void testSubtractFromImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtractFromImaginary(y);
        Assertions.assertEquals(-3.0, z.getReal());
        Assertions.assertEquals(1.0, z.getImaginary());

        Assertions.assertEquals(z, ofImaginary(y).subtract(x));
    }

    @Test
    void testSubtractFromImaginaryNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.subtractFromImaginary(y);
        Assertions.assertEquals(-3.0, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, ofImaginary(y).subtract(x));
    }

    @Test
    void testSubtractFromImaginaryInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        final Complex z = x.subtractFromImaginary(y);
        Assertions.assertEquals(-3.0, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        Assertions.assertEquals(z, ofImaginary(y).subtract(x));
    }

    @Test
    void testSubtractFromImaginaryWithPosZeroReal() {
        final Complex x = Complex.ofCartesian(0.0, 4.0);
        final double y = 5.0;
        final Complex z = x.subtractFromImaginary(y);
        Assertions.assertEquals(-0.0, z.getReal(), "Expected sign inversion");
        Assertions.assertEquals(1.0, z.getImaginary());

        Assertions.assertNotEquals(z, ofImaginary(y).subtract(x));
    }

    @Test
    void testMultiply() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(5.0, 6.0);
        final Complex z = x.multiply(y);
        Assertions.assertEquals(-9.0, z.getReal());
        Assertions.assertEquals(38.0, z.getImaginary());
    }

    @Test
    void testMultiplyInfInf() {
        final Complex z = infInf.multiply(infInf);

        Assertions.assertTrue(z.isInfinite());


        Assertions.assertEquals(Complex.ofCartesian(nan, inf), infInf.multiply(infInf));
        Assertions.assertEquals(Complex.ofCartesian(inf, nan), infInf.multiply(infNegInf));
        Assertions.assertEquals(Complex.ofCartesian(-inf, nan), infInf.multiply(negInfInf));
        Assertions.assertEquals(Complex.ofCartesian(nan, -inf), infInf.multiply(negInfNegInf));
    }

    @Test
    void testMultiplyReal() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 2.0;
        Complex z = x.multiply(y);
        Assertions.assertEquals(6.0, z.getReal());
        Assertions.assertEquals(8.0, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(y)));

        z = x.multiply(-y);
        Assertions.assertEquals(-6.0, z.getReal());
        Assertions.assertEquals(-8.0, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(-y)));
    }

    @Test
    void testMultiplyRealNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.multiply(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(y)));
    }

    @Test
    void testMultiplyRealInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        Complex z = x.multiply(y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(y)));

        z = x.multiply(-y);
        Assertions.assertEquals(-inf, z.getReal());
        Assertions.assertEquals(-inf, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(-y)));
    }

    @Test
    void testMultiplyRealZero() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 0.0;
        Complex z = x.multiply(y);
        Assertions.assertEquals(0.0, z.getReal());
        Assertions.assertEquals(0.0, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofReal(y)));

        z = x.multiply(-y);
        Assertions.assertEquals(-0.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary());

        final Complex z2 = x.multiply(ofReal(-y));
        Assertions.assertEquals(-0.0, z2.getReal());
        Assertions.assertEquals(0.0, z2.getImaginary(), "Expected no sign preservation");
    }

    @Test
    void testMultiplyImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 2.0;
        Complex z = x.multiplyImaginary(y);
        Assertions.assertEquals(-8.0, z.getReal());
        Assertions.assertEquals(6.0, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofImaginary(y)));

        z = x.multiplyImaginary(-y);
        Assertions.assertEquals(8.0, z.getReal());
        Assertions.assertEquals(-6.0, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofImaginary(-y)));
    }

    @Test
    void testMultiplyImaginaryNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.multiplyImaginary(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofImaginary(y)));
    }

    @Test
    void testMultiplyImaginaryInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        Complex z = x.multiplyImaginary(y);
        Assertions.assertEquals(-inf, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofImaginary(y)));

        z = x.multiplyImaginary(-y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(-inf, z.getImaginary());

        Assertions.assertEquals(z, x.multiply(ofImaginary(-y)));
    }

    @Test
    void testMultiplyImaginaryZero() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 0.0;
        Complex z = x.multiplyImaginary(y);
        Assertions.assertEquals(-0.0, z.getReal());
        Assertions.assertEquals(0.0, z.getImaginary());

        Complex z2 = x.multiply(ofImaginary(y));
        Assertions.assertEquals(0.0, z2.getReal(), "Expected no sign preservation");
        Assertions.assertEquals(0.0, z2.getImaginary());

        z = x.multiplyImaginary(-y);
        Assertions.assertEquals(0.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary());

        z2 = x.multiply(ofImaginary(-y));
        Assertions.assertEquals(0.0, z2.getReal());
        Assertions.assertEquals(0.0, z2.getImaginary(), "Expected no sign preservation");
    }

    @Test
    void testNonZeroMultiplyI() {
        final double[] parts = {3.0, 4.0};
        for (final double a : parts) {
            for (final double b : parts) {
                final Complex c = Complex.ofCartesian(a, b);
                final Complex x = c.multiplyImaginary(1.0);

                Assertions.assertEquals(-b, x.getReal());
                Assertions.assertEquals(a, x.getImaginary());
                final Complex z = c.multiply(Complex.I);
                Assertions.assertEquals(x, z);
            }
        }
    }

    @Test
    void testNonZeroMultiplyNegativeI() {

        final double[] parts = {3.0, 4.0};
        final Complex[] negIs = {Complex.ofCartesian(-0.0, -1), Complex.ofCartesian(0.0, -1)};
        for (final double a : parts) {
            for (final double b : parts) {
                final Complex c = Complex.ofCartesian(a, b);
                final Complex x = c.multiplyImaginary(-1.0);

                Assertions.assertEquals(b, x.getReal());
                Assertions.assertEquals(-a, x.getImaginary());
                for (final Complex negI : negIs) {
                    final Complex z = c.multiply(negI);
                    Assertions.assertEquals(x, z);
                }
            }
        }
    }

    @Test
    void testMultiplyZeroByI() {
        final double[] zeros = {-0.0, 0.0};
        for (final double a : zeros) {
            for (final double b : zeros) {
                final Complex c = Complex.ofCartesian(a, b);
                final Complex x = c.multiplyImaginary(1.0);

                Assertions.assertEquals(-b, x.getReal());
                Assertions.assertEquals(a, x.getImaginary());
                final Complex z = c.multiply(Complex.I);

                if (Double.compare(b, 0.0) == 0) {



                    Assertions.assertEquals(0, z.getReal(), 0.0);
                    Assertions.assertEquals(0, z.getImaginary(), 0.0);
                    Assertions.assertNotEquals(x, z);
                } else {
                    Assertions.assertEquals(x, z);
                }
            }
        }
    }

    @Test
    void testMultiplyZeroByNegativeI() {


        final Complex negI = Complex.I.negate();
        final double[] zeros = {-0.0, 0.0};
        for (final double a : zeros) {
            for (final double b : zeros) {
                final Complex c = Complex.ofCartesian(a, b);
                final Complex x = c.multiplyImaginary(-1.0);

                Assertions.assertEquals(b, x.getReal());
                Assertions.assertEquals(-a, x.getImaginary());
                final Complex z = c.multiply(negI);
                final Complex z2 = c.multiply(Complex.I).negate();

                if (Double.compare(b, -0.0) == 0) {



                    Assertions.assertEquals(0, z.getReal(), 0.0);
                    Assertions.assertEquals(0, z.getImaginary(), 0.0);
                    Assertions.assertNotEquals(x, z);


                    Assertions.assertEquals(x, z2);
                } else {
                    Assertions.assertEquals(x, z);


                    Assertions.assertNotEquals(x, z2);
                }
            }
        }
    }

    @Test
    void testDivide() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(5.0, 6.0);
        final Complex z = x.divide(y);
        Assertions.assertEquals(39.0 / 61.0, z.getReal());
        Assertions.assertEquals(2.0 / 61.0, z.getImaginary());
    }

    @Test
    void testDivideZero() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex z = x.divide(Complex.ZERO);
        Assertions.assertEquals(INF, z);
    }

    @Test
    void testDivideZeroZero() {
        final Complex x = Complex.ofCartesian(0.0, 0.0);
        final Complex z = x.divide(Complex.ZERO);
        Assertions.assertEquals(NAN, z);
    }

    @Test
    void testDivideNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex z = x.divide(NAN);
        Assertions.assertTrue(z.isNaN());
    }

    @Test
    void testDivideNanInf() {
        Complex z = oneInf.divide(Complex.ONE);
        Assertions.assertTrue(Double.isNaN(z.getReal()));
        Assertions.assertEquals(inf, z.getImaginary());

        z = negInfNegInf.divide(oneNan);
        Assertions.assertTrue(Double.isNaN(z.getReal()));
        Assertions.assertTrue(Double.isNaN(z.getImaginary()));

        z = negInfInf.divide(Complex.ONE);
        Assertions.assertTrue(Double.isInfinite(z.getReal()));
        Assertions.assertTrue(Double.isInfinite(z.getImaginary()));
    }

    @Test
    void testDivideReal() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 2.0;
        Complex z = x.divide(y);
        Assertions.assertEquals(1.5, z.getReal());
        Assertions.assertEquals(2.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(y)));

        z = x.divide(-y);
        Assertions.assertEquals(-1.5, z.getReal());
        Assertions.assertEquals(-2.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(-y)));
    }

    @Test
    void testDivideRealNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.divide(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(y)));
    }

    @Test
    void testDivideRealInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        Complex z = x.divide(y);
        Assertions.assertEquals(0.0, z.getReal());
        Assertions.assertEquals(0.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(y)));

        z = x.divide(-y);
        Assertions.assertEquals(-0.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(-y)));
    }

    @Test
    void testDivideRealZero() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 0.0;
        Complex z = x.divide(y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(y)));

        z = x.divide(-y);
        Assertions.assertEquals(-inf, z.getReal());
        Assertions.assertEquals(-inf, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofReal(-y)));
    }

    @Test
    void testDivideImaginary() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 2.0;
        Complex z = x.divideImaginary(y);
        Assertions.assertEquals(2.0, z.getReal());
        Assertions.assertEquals(-1.5, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofImaginary(y)));

        z = x.divideImaginary(-y);
        Assertions.assertEquals(-2.0, z.getReal());
        Assertions.assertEquals(1.5, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofImaginary(-y)));
    }

    @Test
    void testDivideImaginaryNaN() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = nan;
        final Complex z = x.divideImaginary(y);
        Assertions.assertEquals(nan, z.getReal());
        Assertions.assertEquals(nan, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofImaginary(y)));
    }

    @Test
    void testDivideImaginaryInf() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = inf;
        Complex z = x.divideImaginary(y);
        Assertions.assertEquals(0.0, z.getReal());
        Assertions.assertEquals(-0.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofImaginary(y)));

        z = x.divideImaginary(-y);
        Assertions.assertEquals(-0.0, z.getReal());
        Assertions.assertEquals(0.0, z.getImaginary());

        Assertions.assertEquals(z, x.divide(ofImaginary(-y)));
    }

    @Test
    void testDivideImaginaryZero() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final double y = 0.0;
        Complex z = x.divideImaginary(y);
        Assertions.assertEquals(inf, z.getReal());
        Assertions.assertEquals(-inf, z.getImaginary());

        Complex z2 = x.divide(ofImaginary(y));
        Assertions.assertEquals(inf, z2.getReal());
        Assertions.assertEquals(inf, z2.getImaginary(), "Expected no sign preservation");

        z = x.divideImaginary(-y);
        Assertions.assertEquals(-inf, z.getReal());
        Assertions.assertEquals(inf, z.getImaginary());

        z2 = x.divide(ofImaginary(-y));
        Assertions.assertEquals(inf, z2.getReal(), "Expected no sign preservation");
        Assertions.assertEquals(inf, z2.getImaginary());
    }









    @Test
    void testSignedArithmetic() {

























        assertSignedZeroArithmetic("addReal", Complex::add, ComplexTest::ofReal, Complex::add,
            0b1111000000000000111100000000000011110000000000001111L);

        assertSignedZeroArithmetic("addImaginary", Complex::addImaginary, ComplexTest::ofImaginary, Complex::add,
            0b1111111111111111L);

        assertSignedZeroArithmetic("subtractReal", Complex::subtract, ComplexTest::ofReal, Complex::subtract, 0);

        assertSignedZeroArithmetic("subtractImaginary", Complex::subtractImaginary, ComplexTest::ofImaginary,
            Complex::subtract, 0);

        assertSignedZeroArithmetic("subtractFromReal", Complex::subtractFrom, ComplexTest::ofReal,
            (y, z) -> z.subtract(y), 0b11110000000000001111000000000000111100000000000011110000L);

        assertSignedZeroArithmetic("subtractFromImaginary", Complex::subtractFromImaginary, ComplexTest::ofImaginary,
            (y, z) -> z.subtract(y), 0b11111111111111110000000000000000L);









        assertSignedZeroArithmetic("multiplyReal", Complex::multiply, ComplexTest::ofReal, Complex::multiply,
            0b1001101011011000000100000001000010111010111110000101000001010L);









        assertSignedZeroArithmetic("multiplyImaginary", Complex::multiplyImaginary, ComplexTest::ofImaginary,
            Complex::multiply, 0b11000110110101001000000010000001110001111101011010000010100000L);





        assertSignedZeroArithmetic("divideReal", Complex::divide, ComplexTest::ofReal, Complex::divide,
            0b100100001000000010000001000000011001000L);



    }

    private static void assertSignedZeroArithmetic(String name, BiFunction<Complex, Double, Complex> doubleOperation,
        DoubleFunction<Complex> doubleToComplex, BiFunction<Complex, Complex, Complex> complexOperation,
        long expectedFailures) {

        final double[] arguments = {-0.0, 0.0, -2, 3};
        for (final double a : arguments) {
            for (final double b : arguments) {
                final Complex c = Complex.ofCartesian(a, b);
                for (final double arg : arguments) {
                    final Complex y = doubleOperation.apply(c, arg);
                    final Complex z = complexOperation.apply(c, doubleToComplex.apply(arg));
                    final boolean expectedFailure = (expectedFailures & 0x1) == 1;
                    expectedFailures >>>= 1;

                    Assertions.assertEquals(y.getReal(), z.getReal(), 0, () -> c + " " + name + " " + arg + ": real");
                    Assertions.assertEquals(y.getImaginary(), z.getImaginary(), 0,
                        () -> c + " " + name + " " + arg + ": imaginary");
                    Assertions.assertEquals(expectedFailure, !y.equals(z),
                        () -> c + " " + name + " " + arg + ": sign-difference");
                }
            }
        }
    }









    @Test
    void testSignedDivideImaginaryArithmetic() {











        long expectedFailures = 0b11001101111011001100110011001110110011110010000111001101000000L;

        final double[] arguments = {-0.0, 0.0, -2, 3};
        for (final double a : arguments) {
            for (final double b : arguments) {
                final Complex c = Complex.ofCartesian(a, b);
                for (final double arg : arguments) {
                    final Complex y = c.divideImaginary(arg);
                    Complex z = c.divide(ofImaginary(arg));
                    final boolean expectedFailure = (expectedFailures & 0x1) == 1;
                    expectedFailures >>>= 1;


                    if (arg == 0) {


                        z = z.multiplyImaginary(1);
                        final double ya = expectedFailure ? Math.abs(y.getReal()) : y.getReal();
                        final double yb = expectedFailure ? Math.abs(y.getImaginary()) : y.getImaginary();
                        final double za = expectedFailure ? Math.abs(z.getReal()) : z.getReal();
                        final double zb = expectedFailure ? Math.abs(z.getImaginary()) : z.getImaginary();
                        Assertions.assertEquals(ya, za, () -> c + " divideImaginary " + arg + ": real");
                        Assertions.assertEquals(yb, zb, () -> c + " divideImaginary " + arg + ": imaginary");
                    } else {

                        Assertions.assertEquals(y.getReal(), z.getReal(), 0,
                            () -> c + " divideImaginary " + arg + ": real");
                        Assertions.assertEquals(y.getImaginary(), z.getImaginary(), 0,
                            () -> c + " divideImaginary " + arg + ": imaginary");
                        Assertions.assertEquals(expectedFailure, !y.equals(z),
                            () -> c + " divideImaginary " + arg + ": sign-difference");
                    }
                }
            }
        }
    }

    @Test
    void testLog10() {
        final double ln10 = Math.log(10);
        final UniformRandomProvider rng = RandomSource.SPLIT_MIX_64.create();
        for (int i = 0; i < 10; i++) {
            final Complex z = Complex.ofCartesian(rng.nextDouble() * 2, rng.nextDouble() * 2);
            final Complex lnz = z.log();
            final Complex log10z = z.log10();

            Assertions.assertEquals(lnz.getReal() / ln10, log10z.getReal(), 1e-12, "real");

            Assertions.assertEquals(lnz.getImaginary(), log10z.getImaginary(), "imag");
        }
    }

    @Test
    void testPow() {
        final Complex x = Complex.ofCartesian(3, 4);
        final double yDouble = 5.0;
        final Complex yComplex = ofReal(yDouble);
        Assertions.assertEquals(x.pow(yComplex), x.pow(yDouble));
    }

    @Test
    void testPowComplexRealZero() {

        final Complex x = Complex.ofCartesian(0, 1);
        final Complex z = Complex.ofCartesian(2, 3);
        final Complex c = x.pow(z);

        Assertions.assertEquals(-0.008983291021129429, c.getReal());
        Assertions.assertEquals(1.1001358594835313e-18, c.getImaginary());
    }

    @Test
    void testPowComplexZeroBase() {
        final double x = Double.MIN_VALUE;
        assertPowComplexZeroBase(0, 0, NAN);
        assertPowComplexZeroBase(0, x, NAN);
        assertPowComplexZeroBase(x, x, NAN);
        assertPowComplexZeroBase(x, 0, Complex.ZERO);
    }

    private static void assertPowComplexZeroBase(double re, double im, Complex expected) {
        final Complex z = Complex.ofCartesian(re, im);
        final Complex c = Complex.ZERO.pow(z);
        Assertions.assertEquals(expected, c);
    }

    @Test
    void testPowScalerRealZero() {

        final Complex x = Complex.ofCartesian(0, 1);
        final Complex c = x.pow(2);

        Assertions.assertEquals(-1, c.getReal());
        Assertions.assertEquals(1.2246467991473532e-16, c.getImaginary());
    }

    @Test
    void testPowScalarZeroBase() {
        final double x = Double.MIN_VALUE;
        assertPowScalarZeroBase(0, NAN);
        assertPowScalarZeroBase(x, Complex.ZERO);
    }

    private static void assertPowScalarZeroBase(double exp, Complex expected) {
        final Complex c = Complex.ZERO.pow(exp);
        Assertions.assertEquals(expected, c);
    }

    @Test
    void testPowNanBase() {
        final Complex x = NAN;
        final double yDouble = 5.0;
        final Complex yComplex = ofReal(yDouble);
        Assertions.assertEquals(x.pow(yComplex), x.pow(yDouble));
    }

    @Test
    void testPowNanExponent() {
        final Complex x = Complex.ofCartesian(3, 4);
        final double yDouble = Double.NaN;
        final Complex yComplex = ofReal(yDouble);
        Assertions.assertEquals(x.pow(yComplex), x.pow(yDouble));
    }

    @Test
    void testSqrtPolar() {
        final double tol = 1e-12;
        double r = 1;
        for (int i = 0; i < 5; i++) {
            r += i;
            double theta = 0;
            for (int j = 0; j < 11; j++) {
                theta += pi / 12;
                final Complex z = Complex.ofPolar(r, theta);
                final Complex sqrtz = Complex.ofPolar(Math.sqrt(r), theta / 2);
                TestUtils.assertEquals(sqrtz, z.sqrt(), tol);
            }
        }
    }

    @Test
    void testZerothRootThrows() {
        final Complex c = Complex.ofCartesian(1, 1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.nthRoot(0),
            "zeroth root should not be allowed");
    }













    @Test
    void testNthRootNormalThirdRoot() {

        final Complex z = Complex.ofCartesian(-2, 2);

        final Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);

        Assertions.assertEquals(3, thirdRootsOfZ.length);

        Assertions.assertEquals(1.0, thirdRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(1.0, thirdRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1.3660254037844386, thirdRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(0.36602540378443843, thirdRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0.366025403784439, thirdRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(-1.3660254037844384, thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }














    @Test
    void testNthRootNormalFourthRoot() {

        final Complex z = Complex.ofCartesian(5, -2);

        final Complex[] fourthRootsOfZ = z.nthRoot(4).toArray(new Complex[0]);

        Assertions.assertEquals(4, fourthRootsOfZ.length);

        Assertions.assertEquals(1.5164629308487783, fourthRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(-0.14469266210702247, fourthRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0.14469266210702256, fourthRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(1.5164629308487783, fourthRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1.5164629308487783, fourthRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(0.14469266210702267, fourthRootsOfZ[2].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-0.14469266210702275, fourthRootsOfZ[3].getReal(), 1.0e-5);
        Assertions.assertEquals(-1.5164629308487783, fourthRootsOfZ[3].getImaginary(), 1.0e-5);
    }













    @Test
    void testNthRootCornercaseThirdRootImaginaryPartEmpty() {


        final Complex z = Complex.ofCartesian(8, 0);

        final Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);

        Assertions.assertEquals(3, thirdRootsOfZ.length);

        Assertions.assertEquals(2.0, thirdRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(0.0, thirdRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1.0, thirdRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(1.7320508075688774, thirdRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1.0, thirdRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(-1.732050807568877, thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }













    @Test
    void testNthRootCornercaseThirdRootRealPartZero() {

        final Complex z = Complex.ofCartesian(0, 2);

        final Complex[] thirdRootsOfZ = z.nthRoot(3).toArray(new Complex[0]);

        Assertions.assertEquals(3, thirdRootsOfZ.length);

        Assertions.assertEquals(1.0911236359717216, thirdRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(0.6299605249474365, thirdRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1.0911236359717216, thirdRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(0.6299605249474365, thirdRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-2.3144374213981936E-16, thirdRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(-1.2599210498948732, thirdRootsOfZ[2].getImaginary(), 1.0e-5);
    }
















    @Test
    void testNthRootNegativeArg() {

        final Complex z = Complex.ofCartesian(1, 0);

        Complex[] fourthRootsOfZ = z.nthRoot(4).toArray(new Complex[0]);

        Assertions.assertEquals(1, fourthRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(0, fourthRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0, fourthRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(1, fourthRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1, fourthRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(0, fourthRootsOfZ[2].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0, fourthRootsOfZ[3].getReal(), 1.0e-5);
        Assertions.assertEquals(-1, fourthRootsOfZ[3].getImaginary(), 1.0e-5);

        fourthRootsOfZ = z.nthRoot(-4).toArray(new Complex[0]);

        Assertions.assertEquals(1, fourthRootsOfZ[0].getReal(), 1.0e-5);
        Assertions.assertEquals(0, fourthRootsOfZ[0].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0, fourthRootsOfZ[1].getReal(), 1.0e-5);
        Assertions.assertEquals(-1, fourthRootsOfZ[1].getImaginary(), 1.0e-5);

        Assertions.assertEquals(-1, fourthRootsOfZ[2].getReal(), 1.0e-5);
        Assertions.assertEquals(0, fourthRootsOfZ[2].getImaginary(), 1.0e-5);

        Assertions.assertEquals(0, fourthRootsOfZ[3].getReal(), 1.0e-5);
        Assertions.assertEquals(1, fourthRootsOfZ[3].getImaginary(), 1.0e-5);
    }

    @Test
    void testNthRootNan() {
        final int n = 3;
        final Complex z = ofReal(Double.NaN);
        final List<Complex> r = z.nthRoot(n);
        Assertions.assertEquals(n, r.size());
        for (final Complex c : r) {
            Assertions.assertTrue(Double.isNaN(c.getReal()));
            Assertions.assertTrue(Double.isNaN(c.getImaginary()));
        }
    }

    @Test
    void testNthRootInf() {
        final int n = 3;
        final Complex z = ofReal(Double.NEGATIVE_INFINITY);
        final List<Complex> r = z.nthRoot(n);
        Assertions.assertEquals(n, r.size());
    }

    @Test
    void testEqualsWithNull() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertNotEquals(x, null);
    }

    @Test
    void testEqualsWithAnotherClass() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertNotEquals(x, new Object());
    }

    @Test
    void testEqualsWithSameObject() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertEquals(x, x);
    }

    @Test
    void testEqualsWithCopyObject() {
        final Complex x = Complex.ofCartesian(3.0, 4.0);
        final Complex y = Complex.ofCartesian(3.0, 4.0);
        Assertions.assertEquals(x, y);
    }

    @Test
    void testEqualsWithRealDifference() {
        final Complex x = Complex.ofCartesian(0.0, 0.0);
        final Complex y = Complex.ofCartesian(0.0 + Double.MIN_VALUE, 0.0);
        Assertions.assertNotEquals(x, y);
    }

    @Test
    void testEqualsWithImaginaryDifference() {
        final Complex x = Complex.ofCartesian(0.0, 0.0);
        final Complex y = Complex.ofCartesian(0.0, 0.0 + Double.MIN_VALUE);
        Assertions.assertNotEquals(x, y);
    }






    @Test
    void testEqualsIsConsistentWithArraysEquals() {

        assertEqualsIsConsistentWithArraysEquals(Complex.ofCartesian(Double.NaN, 0.0),
            Complex.ofCartesian(Double.NaN, 1.0), "NaN real and different non-NaN imaginary");
        assertEqualsIsConsistentWithArraysEquals(Complex.ofCartesian(0.0, Double.NaN),
            Complex.ofCartesian(1.0, Double.NaN), "Different non-NaN real and NaN imaginary");
        assertEqualsIsConsistentWithArraysEquals(Complex.ofCartesian(0.0, 0.0), Complex.ofCartesian(-0.0, 0.0),
            "Different real zeros");
        assertEqualsIsConsistentWithArraysEquals(Complex.ofCartesian(0.0, 0.0), Complex.ofCartesian(0.0, -0.0),
            "Different imaginary zeros");


        final double[] values = {Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, -1, 0, 1};
        final ArrayList<Complex> list = createCombinations(values);

        for (final Complex c : list) {
            final double real = c.getReal();
            final double imag = c.getImaginary();


            assertEqualsIsConsistentWithArraysEquals(c, Complex.ofCartesian(real, imag), "Copy complex");


            final double realDelta = smallestChange(real);
            final double imagDelta = smallestChange(imag);
            Assertions.assertNotEquals(real, realDelta, "Real was not changed");
            Assertions.assertNotEquals(imag, imagDelta, "Imaginary was not changed");

            assertEqualsIsConsistentWithArraysEquals(c, Complex.ofCartesian(realDelta, imag), "Delta real");
            assertEqualsIsConsistentWithArraysEquals(c, Complex.ofCartesian(real, imagDelta), "Delta imaginary");
        }
    }





    @Test
    void testEqualsWithDifferentNaNs() {

        final double[] values = {Double.NaN, 0, 1};
        final ArrayList<Complex> list = createCombinations(values);





        for (int i = 0; i < list.size(); i++) {
            final Complex c1 = list.get(i);
            final Complex copy = Complex.ofCartesian(c1.getReal(), c1.getImaginary());
            assertEqualsIsConsistentWithArraysEquals(c1, copy, "Copy is not equal");
            for (int j = i + 1; j < list.size(); j++) {
                final Complex c2 = list.get(j);
                assertEqualsIsConsistentWithArraysEquals(c1, c2, "Different NaNs should not be equal");
            }
        }
    }









    private static void assertEqualsIsConsistentWithArraysEquals(Complex c1, Complex c2, String msg) {
        final boolean expected = Arrays.equals(new double[] {c1.getReal(), c1.getImaginary()},
            new double[] {c2.getReal(), c2.getImaginary()});
        final boolean actual = c1.equals(c2);
        Assertions.assertEquals(expected, actual,
            () -> String.format("equals(Object) is not consistent with Arrays.equals: %s. %s vs %s", msg, c1, c2));
    }







    @Test
    void testHashCode() {

        final double[] values = {Double.NaN, Double.NEGATIVE_INFINITY, -3.45, -1, -0.0, 0.0, Double.MIN_VALUE, 1, 3.45,
            Double.POSITIVE_INFINITY};
        final ArrayList<Complex> list = createCombinations(values);

        final String msg = "'equals' not compatible with 'hashCode'";

        for (final Complex c : list) {
            final double real = c.getReal();
            final double imag = c.getImaginary();
            final int expected = Arrays.hashCode(new double[] {real, imag});
            final int hash = c.hashCode();
            Assertions.assertEquals(expected, hash, "hashCode does not match Arrays.hashCode({re, im})");



            final Complex copy = Complex.ofCartesian(real, imag);
            Assertions.assertEquals(hash, copy.hashCode(), "Copy hash code is not equal");






            final double realDelta = smallestChange(real);
            final double imagDelta = smallestChange(imag);
            Assertions.assertNotEquals(real, realDelta, "Real was not changed");
            Assertions.assertNotEquals(imag, imagDelta, "Imaginary was not changed");

            final Complex cRealDelta = Complex.ofCartesian(realDelta, imag);
            final Complex cImagDelta = Complex.ofCartesian(real, imagDelta);
            if (hash != cRealDelta.hashCode()) {
                Assertions.assertNotEquals(c, cRealDelta, () -> "real+delta: " + msg);
            }
            if (hash != cImagDelta.hashCode()) {
                Assertions.assertNotEquals(c, cImagDelta, () -> "imaginary+delta: " + msg);
            }
        }
    }












    @Test
    void testHashCodeWithDifferentZeros() {
        final double[] values = {-0.0, 0.0};
        final ArrayList<Complex> list = createCombinations(values);



        for (int i = 0; i < list.size(); i++) {
            final Complex c1 = list.get(i);
            for (int j = i + 1; j < list.size(); j++) {
                final Complex c2 = list.get(j);
                if (c1.hashCode() != c2.hashCode()) {
                    Assertions.assertNotEquals(c1, c2, "'equals' not compatible with 'hashCode'");
                }
            }
        }
    }








    private static ArrayList<Complex> createCombinations(final double[] values) {
        final ArrayList<Complex> list = new ArrayList<>(values.length * values.length);
        for (final double re : values) {
            for (final double im : values) {
                list.add(Complex.ofCartesian(re, im));
            }
        }
        return list;
    }










    private static double smallestChange(double x) {
        if (Double.isNaN(x)) {
            return 0;
        }
        return x == Double.POSITIVE_INFINITY ? Math.nextDown(x) : Math.nextUp(x);
    }

    @Test
    void testAtanhEdgeConditions() {

        final Complex c = Complex.ofCartesian(2, 0).atanh();

        Assertions.assertEquals(0.54930614433405489, c.getReal());
        Assertions.assertEquals(1.5707963267948966, c.getImaginary());
    }

    @Test
    void testAtanhAssumptions() {

        final double safeUpper = Math.sqrt(Double.MAX_VALUE) / 2;
        final double safeLower = Math.sqrt(Double.MIN_NORMAL) * 2;


        Assertions.assertEquals(safeUpper, 1 + safeUpper);

        Assertions.assertEquals(-safeUpper, 1 - safeUpper);

        Assertions.assertEquals(0, safeLower * safeLower / safeUpper);

        Assertions.assertEquals(safeUpper, 1 / safeUpper + safeUpper);

        Assertions.assertEquals(4, 4 + safeLower * safeLower);

        Assertions.assertEquals(1, (1 - safeLower) * (1 - safeLower));

        Assertions.assertEquals(1, 1 - safeLower * safeLower);


        final double result = 4 * safeLower / safeUpper / safeUpper;
        Assertions.assertEquals(result, Math.log1p(result));
        Assertions.assertEquals(result, result - result * result / 2, "Expected log1p Taylor series to be redundant");

        Assertions.assertNotEquals(0, 1 - Math.nextUp(1));
        Assertions.assertNotEquals(0, 1 - Math.nextDown(1));
    }

    @Test
    void testCoshSinhTanhAssumptions() {

        final double safeExpMax = 708;

        final double big = Math.exp(safeExpMax);
        final double small = Math.exp(-safeExpMax);


        Assertions.assertTrue(Double.isFinite(big));
        Assertions.assertTrue(Double.isInfinite(Math.exp(safeExpMax + 2)));


        Assertions.assertEquals(big + small, big);
        Assertions.assertEquals(Math.cosh(safeExpMax), big / 2);
        Assertions.assertEquals(Math.cosh(-safeExpMax), big / 2);


        Assertions.assertEquals(big - small, big);
        Assertions.assertEquals(small - big, -big);
        Assertions.assertEquals(Math.sinh(safeExpMax), big / 2);
        Assertions.assertEquals(Math.sinh(-safeExpMax), -big / 2);



        Assertions.assertTrue(Double.isFinite(Math.sinh(safeExpMax / 2) * Math.cosh(safeExpMax / 2)));
        Assertions.assertTrue(Double.isFinite(Math.sinh(safeExpMax / 2) * Math.sinh(safeExpMax / 2)));


        Assertions.assertNotEquals(0.0, 2.0 / big);
        Assertions.assertEquals(0.0, 2.0 / big / big);



        Assertions.assertTrue(Double.isFinite(0.5 * big * Double.MIN_VALUE * big));
        Assertions.assertTrue(Double.isInfinite(0.5 * big * Double.MIN_VALUE * big * big));


        for (final double y : new double[] {Math.PI / 2, Math.PI / 4, 1.0, 0.5, 0.0}) {
            Assertions.assertEquals(Math.signum(Math.sin(2 * y)), Math.signum(Math.sin(y) * Math.cos(y)));
            Assertions.assertEquals(Math.signum(Math.sin(2 * -y)), Math.signum(Math.sin(-y) * Math.cos(-y)));
        }



        Assertions.assertTrue(2.0 / Double.MAX_VALUE > 0);
    }





    @Test
    void testSinCosLinearAssumptions() {


        Assertions.assertEquals(1.0, Math.cos(Double.MIN_NORMAL));
        Assertions.assertEquals(Double.MIN_NORMAL, Math.sin(Double.MIN_NORMAL));



        Assertions.assertEquals(1.0, Math.cosh(Double.MIN_NORMAL));
        Assertions.assertEquals(Double.MIN_NORMAL, Math.sinh(Double.MIN_NORMAL));
    }











    @Test
    void testAbsVsSqrt() {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();













        assertAbsVsSqrt(1000,
            () -> Complex.ofCartesian(createFixedExponentNumber(rng, 1000), createFixedExponentNumber(rng, 1000)));
        assertAbsVsSqrt(1000,
            () -> Complex.ofCartesian(createFixedExponentNumber(rng, -1000), createFixedExponentNumber(rng, -1000)));
    }

    private static void assertAbsVsSqrt(int samples, Supplier<Complex> supplier) {




        for (int i = 0; i < samples; i++) {
            final Complex z = supplier.get();
            final double abs = z.abs();
            final double x = Math.abs(z.getReal());
            final double y = Math.abs(z.getImaginary());










            final Complex c = z.sqrt();
            final double t = Math.sqrt(2 * (x + abs));
            if (z.getReal() >= 0) {
                Assertions.assertEquals(t / 2, c.getReal());
                Assertions.assertEquals(z.getImaginary() / t, c.getImaginary());
            } else {
                Assertions.assertEquals(y / t, c.getReal());
                Assertions.assertEquals(Math.copySign(t / 2, z.getImaginary()), c.getImaginary());
            }
        }
    }











    @Test
    void testAbsVsLog() {
        final UniformRandomProvider rng = RandomSource.XO_RO_SHI_RO_128_PP.create();















        assertAbsVsLog(100,
            () -> Complex.ofCartesian(createFixedExponentNumber(rng, 1022), createFixedExponentNumber(rng, 1022)));
        assertAbsVsLog(100,
            () -> Complex.ofCartesian(createFixedExponentNumber(rng, -1022), createFixedExponentNumber(rng, -1022)));
    }

    private static void assertAbsVsLog(int samples, Supplier<Complex> supplier) {



        for (int i = 0; i < samples; i++) {
            final Complex z = supplier.get();
            final double abs = z.abs();


            final Complex c = z.log();
            Assertions.assertEquals(Math.log(abs), c.getReal());
        }
    }









    private static double createFixedExponentNumber(UniformRandomProvider rng, int exponent) {
        return Double.longBitsToDouble((rng.nextLong() >>> 12) | ((1023L + exponent) << 52));
    }
}
