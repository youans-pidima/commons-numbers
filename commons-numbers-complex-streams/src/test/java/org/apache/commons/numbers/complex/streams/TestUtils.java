
















package org.apache.commons.numbers.complex.streams;

import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.numbers.core.Precision;

import org.junit.jupiter.api.Assertions;




@SuppressWarnings("WeakerAccess")
final class TestUtils {



    private TestUtils() {
        super();
    }





    public static void assertSame(String msg, Complex[] expected, Complex[] actual) {
        assertEquals(msg, expected, actual, 0);
    }





    public static void assertSame(String msg, Complex[][] expected, Complex[][] actual) {
        assertEquals(msg, expected, actual, 0);
    }





    public static void assertSame(String msg, Complex[][][] expected, Complex[][][] actual) {
        assertEquals(msg, expected, actual, 0);
    }





    public static void assertSame(String msg, Complex[][][][] expected, Complex[][][][] actual) {
        assertEquals(msg, expected, actual, 0);
    }





    public static void assertSame(Complex expected, Complex actual) {
        Assertions.assertEquals(expected.getReal(), actual.getReal());
        Assertions.assertEquals(expected.getImaginary(), actual.getImaginary());
    }





    public static void assertEquals(Complex expected, Complex actual, double delta) {
        Assertions.assertEquals(expected.getReal(), actual.getReal(), delta, "Real Values Differ");
        Assertions.assertEquals(expected.getImaginary(), actual.getImaginary(), delta, "Imaginary Values Differ");
    }


    public static void assertEquals(String msg, double[][] expected, double[][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertArrayEquals(expected[i], observed[i], tolerance, msg + "[" + i + "]");
        }
    }


    public static void assertEquals(String msg, double[][][] expected, double[][][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, double[][][][] expected, double[][][][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, float[][][][] expected, float[][][][] observed, float tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, float[][] expected, float[][] observed, float tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertArrayEquals(expected[i], observed[i], tolerance, msg + "[" + i + "]");
        }
    }


    public static void assertEquals(String msg, float[][][] expected, float[][][] observed, float tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, Complex[] expected, Complex[] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        StringBuilder out = new StringBuilder(msg);
        boolean failure = false;
        for (int i = 0; i < expected.length; i++) {
            if (!Precision.equalsIncludingNaN(expected[i].getReal(), observed[i].getReal(), tolerance)) {
                failure = true;
                out.append("\n[").append(i).append("] ");
                out.append("Real elements differ. ");
                out.append(" expected = ");
                out.append(expected[i].getReal());
                out.append(" observed = ");
                out.append(observed[i].getReal());
            }
            if (!Precision.equalsIncludingNaN(expected[i].getImaginary(), observed[i].getImaginary(), tolerance)) {
                failure = true;
                out.append("\n[").append(i).append("] ");
                out.append("Imaginary elements differ. ");
                out.append(" expected = ");
                out.append(expected[i].getImaginary());
                out.append(" observed = ");
                out.append(observed[i].getImaginary());
            }
        }
        if (failure) {
            Assertions.fail(out.toString());
        }
    }


    public static void assertEquals(String msg, Complex[][] expected, Complex[][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, Complex[][][] expected, Complex[][][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }


    public static void assertEquals(String msg, Complex[][][][] expected, Complex[][][][] observed, double tolerance) {
        assertArrayLengthsEqual(msg, expected.length, observed.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(msg + "[" + i + "]", expected[i], observed[i], tolerance);
        }
    }







    private static void assertArrayLengthsEqual(String msg, int expectedLength, int observedLength) {
        if (expectedLength != observedLength) {
            StringBuilder out = new StringBuilder(msg);
            if (msg != null && msg.length() > 0) {
                out.append("\n");
            }
            out.append("Arrays not same length. \n");
            out.append("expected has length ").append(expectedLength);
            out.append(" observed has length = ").append(observedLength);
            Assertions.fail(out.toString());
        }
    }
}
