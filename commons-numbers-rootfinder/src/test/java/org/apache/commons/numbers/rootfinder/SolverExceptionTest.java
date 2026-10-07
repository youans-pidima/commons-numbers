















package org.apache.commons.numbers.rootfinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class SolverExceptionTest {
    @Test
    void testOutOfRangeFormatting() {
        final double x = Double.MIN_NORMAL;
        final double y = Double.MIN_VALUE;
        final double z = 1.0;
        final String msg = new SolverException(SolverException.OUT_OF_RANGE,
                x, y, z).getMessage();
        Assertions.assertTrue(msg.contains(String.valueOf(x)), () -> "Missing: " + x);
        Assertions.assertTrue(msg.contains(String.valueOf(y)), () -> "Missing: " + y);
        Assertions.assertTrue(msg.contains(String.valueOf(z)), () -> "Missing: " + z);
    }

    @Test
    void testBracketingFormatting() {
        final double w = Double.MAX_VALUE;
        final double x = Double.MIN_NORMAL;
        final double y = Double.MIN_VALUE;
        final double z = 1.0;
        final String msg = new SolverException(SolverException.BRACKETING,
                w, x, y, z).getMessage();
        Assertions.assertTrue(msg.contains(String.valueOf(w)), () -> "Missing: " + w);
        Assertions.assertTrue(msg.contains(String.valueOf(x)), () -> "Missing: " + x);
        Assertions.assertTrue(msg.contains(String.valueOf(y)), () -> "Missing: " + y);
        Assertions.assertTrue(msg.contains(String.valueOf(z)), () -> "Missing: " + z);
    }

    @Test
    void testTooLargeFormatting() {
        final double x = Double.MIN_NORMAL;
        final double y = Double.MIN_VALUE;
        final String msg = new SolverException(SolverException.TOO_LARGE,
                x, y).getMessage();
        Assertions.assertTrue(msg.contains(String.valueOf(x)), () -> "Missing: " + x);
        Assertions.assertTrue(msg.contains(String.valueOf(y)), () -> "Missing: " + y);
    }
}
