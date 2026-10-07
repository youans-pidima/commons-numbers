
















package org.apache.commons.numbers.complex;

import org.apache.commons.numbers.complex.TestUtils.TestDataFlagOption;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;











class CReferenceTest {

    private static final long POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(+0.0);

    private static final long NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);







































    private static long globalMaxUlps = 0;


    private static boolean reportAllDeviations = false;












    static void assertEquals(Supplier<String> msg, double expected, double actual, long maxUlps) {
        final long e = Double.doubleToLongBits(expected);
        final long a = Double.doubleToLongBits(actual);




        long delta;
        boolean equal;
        if (e == a) {

            equal = true;
            delta = 0;
        } else if ((a ^ e) < 0L) {

            equal = false;


            if (a < e) {
                delta = (e - POSITIVE_ZERO_DOUBLE_BITS) + (a - NEGATIVE_ZERO_DOUBLE_BITS) + 1;
            } else {
                delta = (a - POSITIVE_ZERO_DOUBLE_BITS) + (e - NEGATIVE_ZERO_DOUBLE_BITS) + 1;
            }
        } else {
            delta = Math.abs(e - a);

            equal = delta <= ((maxUlps < 0) ? (-maxUlps - 1) : maxUlps);
        }


        if (maxUlps < 0) {

            if (!equal) {
                System.out.printf("%s: expected <%s> != actual <%s> (ulps=%s)%n",
                        msg.get(), expected, actual, Long.toUnsignedString(delta));
            } else if (reportAllDeviations) {
                System.out.printf("%s: expected <%s> == actual <%s> (ulps=0)%n",
                    msg.get(), expected, actual);
            }

            return;
        }

        if (!equal) {
            Assertions.fail(String.format("%s: expected <%s> != actual <%s> (ulps=%s)",
                    msg.get(), expected, actual, Long.toUnsignedString(delta)));
        }
    }
















    static void assertComplex(Complex c,
            String name, UnaryOperator<Complex> operation,
            Complex expected, long maxUlps) {
        final Complex z = operation.apply(c);
        assertEquals(() -> c + "." + name + "(): real", expected.real(), z.real(), maxUlps);
        assertEquals(() -> c + "." + name + "(): imaginary", expected.imag(), z.imag(), maxUlps);
    }

















    static void assertComplex(Complex c1, Complex c2,
            String name, BiFunction<Complex, Complex, Complex> operation,
            Complex expected, long maxUlps) {
        final Complex z = operation.apply(c1, c2);
        assertEquals(() -> c1 + "." + name + c2 + ": real", expected.real(), z.real(), maxUlps);
        assertEquals(() -> c1 + "." + name + c2 + ": imaginary", expected.imag(), z.imag(), maxUlps);
    }








    private static void assertOperation(String name,
            UnaryOperator<Complex> operation, long maxUlps) {
        final List<Complex[]> data = loadTestData(name);
        final long ulps = getTestUlps(maxUlps);
        for (final Complex[] pair : data) {
            assertComplex(pair[0], name, operation, pair[1], ulps);
        }
    }








    private static void assertBiOperation(String name,
            BiFunction<Complex, Complex, Complex> operation, long maxUlps) {
        final List<Complex[]> data = loadTestData(name);
        final long ulps = getTestUlps(maxUlps);
        for (final Complex[] triple : data) {
            assertComplex(triple[0], triple[1], name, operation, triple[2], ulps);
        }
    }







    private static List<Complex[]> loadTestData(String name) {
        final String testData = "data/" + name + ".txt";
        final TestDataFlagOption option = globalMaxUlps < 1 ?
            TestDataFlagOption.LOAD : TestDataFlagOption.IGNORE;
        return TestUtils.loadTestData(testData, option,

            s -> System.out.println(name + " IGNORED: " + s));

    }








    private static long getTestUlps(long ulps) {


        if ((globalMaxUlps ^ ulps) >= 0) {
            final long max = Math.max(Math.abs(globalMaxUlps), Math.abs(ulps));

            return ulps < 0 ? -max : max;
        }


        if (globalMaxUlps < 0) {
            return globalMaxUlps;
        }


        return ulps;
    }

    @Test
    void testAcos() {
        assertOperation("acos", Complex::acos, 2);
    }

    @Test
    void testAcosh() {
        assertOperation("acosh", Complex::acosh, 2);
    }

    @Test
    void testAsinh() {

        assertOperation("asinh", Complex::asinh, 3);
    }

    @Test
    void testAtanh() {

        assertOperation("atanh", Complex::atanh, 1);
    }

    @Test
    void testCosh() {

        assertOperation("cosh", Complex::cosh, 2);
    }

    @Test
    void testSinh() {

        assertOperation("sinh", Complex::sinh, 2);
    }

    @Test
    void testTanh() {

        assertOperation("tanh", Complex::tanh, 2);
    }

    @Test
    void testExp() {
        assertOperation("exp", Complex::exp, 2);
    }

    @Test
    void testLog() {
        assertOperation("log", Complex::log, 1);
    }

    @Test
    void testSqrt() {
        assertOperation("sqrt", Complex::sqrt, 1);
    }

    @Test
    void testMultiply() {
        assertBiOperation("multiply", Complex::multiply, 0);
    }

    @Test
    void testDivide() {
        assertBiOperation("divide", Complex::divide, 7);
    }

    @Test
    void testPowComplex() {
        assertBiOperation("pow", Complex::pow, 9);
    }
}
