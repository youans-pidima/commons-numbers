















package org.apache.commons.numbers.rootfinder;

import java.time.Duration;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;




class BrentSolverTest {
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1e-6;
    private static final double DEFAULT_RELATIVE_ACCURACY = 1e-14;
    private static final double DEFAULT_FUNCTION_ACCURACY = 1e-15;

    @Test
    void testInvalidAccuracies() {




        for (final double bad : new double[] {Double.NaN, -1, -Double.MIN_VALUE,
            Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BrentSolver(bad, DEFAULT_ABSOLUTE_ACCURACY, DEFAULT_FUNCTION_ACCURACY),
                () -> "relative accuracy: " + bad);
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BrentSolver(DEFAULT_RELATIVE_ACCURACY, bad, DEFAULT_FUNCTION_ACCURACY),
                () -> "absolute accuracy: " + bad);
            Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY, bad),
                () -> "function value accuracy: " + bad);
        }

        Assertions.assertDoesNotThrow(() -> new BrentSolver(0, 0, 0));
    }

    @Test
    void testNonFiniteBracketValue() {
        final DoubleUnaryOperator f = x -> x;
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY,
            DEFAULT_FUNCTION_ACCURACY);
        final SolverException ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, 0, Double.POSITIVE_INFINITY));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("non-finite"));

        final SolverException ex2 = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, Double.NEGATIVE_INFINITY, 0));
        Assertions.assertNotEquals(-1, ex2.getMessage().indexOf("non-finite"));
    }

    @Test
    void testNaNFunctionValue() {




        final DoubleUnaryOperator f = x -> {
            if (x < 0.1) {
                return -1;
            }
            return x < 0.4 ? Double.NaN : 1;
        };
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY, DEFAULT_ABSOLUTE_ACCURACY,
            DEFAULT_FUNCTION_ACCURACY);
        final SolverException ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, 0, 0.05, 0.5));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("NaN"));
    }

    @Test
    void testSinZero() {



        final DoubleUnaryOperator func = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        double result;
        MonitoredFunction f;


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 3, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 7);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 1, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 8);
    }

    @Test
    void testQuinticZero() {







        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        double result;
        MonitoredFunction f;



        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.2, 0.2);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 3);



        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.1, 0.3);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);

        Assertions.assertTrue(f.getCallsCount() <= 7);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, -0.3, 0.45);
        Assertions.assertEquals(0, result, DEFAULT_ABSOLUTE_ACCURACY);

        Assertions.assertTrue(f.getCallsCount() <= 8);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.3, 0.7);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);

        Assertions.assertTrue(f.getCallsCount() <= 9);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.2, 0.6);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 10);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.05, 0.95);
        Assertions.assertEquals(0.5, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);



        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.85, 1.25);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.8, 1.2);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 11);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.85, 1.75);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 13);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.55, 1.45);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 10);
    }

    @Test
    void testTooManyCalls() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);


        final MonitoredFunction f = new MonitoredFunction(func);
        final double result = solver.findRoot(f, 0.85, 5);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() <= 15);

        final MonitoredFunction f2 = new MonitoredFunction(func, 10);
        final IllegalStateException ex = Assertions.assertThrows(IllegalStateException.class,
            () -> solver.findRoot(f2, 0.85, 5), "Expected too many calls condition");

        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("too many calls"));
    }

    @Test
    void testRootEndpoints() {
        final DoubleUnaryOperator f = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);


        double result = solver.findRoot(f, Math.PI, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, 3, Math.PI);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, Math.PI, 3.5, 4);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);

        result = solver.findRoot(f, 3, 3.07, Math.PI);
        Assertions.assertEquals(Math.PI, result, DEFAULT_ABSOLUTE_ACCURACY);
    }

    @Test
    void testBadEndpoints() {
        final DoubleUnaryOperator f = new Sin();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);
        SolverException ex;


        ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, 1, -1));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf(" > "));


        ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, 1, 1.5));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("No bracketing"));

        ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(f, 1, 1.2, 1.5));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("No bracketing"));
    }

    @Test
    void testBadInitialGuess() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        SolverException ex;


        ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(func, 0.0, 7.0, 0.6));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("out of range"));

        ex = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(func, 0.0, -7.0, 0.6));
        Assertions.assertNotEquals(-1, ex.getMessage().indexOf("out of range"));
    }

    @Test
    void testInitialGuess() {
        final DoubleUnaryOperator func = new QuinticFunction();
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);
        double result;
        MonitoredFunction f;


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        final int referenceCallsCount = f.getCallsCount();
        Assertions.assertTrue(referenceCallsCount >= 13);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 0.61, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() > referenceCallsCount);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 0.9999990001, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertTrue(f.getCallsCount() < referenceCallsCount);


        f = new MonitoredFunction(func);
        result = solver.findRoot(f, 0.6, 1.0, 7.0);
        Assertions.assertEquals(1.0, result, DEFAULT_ABSOLUTE_ACCURACY);
        Assertions.assertEquals(1, f.getCallsCount());
    }





    @Test
    void testSubNormalBracket() {
        final double lower = Double.MIN_VALUE;
        final double initial = lower * 5;
        final double upper = lower * 10;


        final BrentSolver solver = new BrentSolver(0.0,
                                                   0.0,
                                                   0.0);

        final double target1 = lower * 2;
        final DoubleUnaryOperator func1 = x -> x - target1;
        final double result1 = solver.findRoot(func1, lower, initial, upper);
        Assertions.assertEquals(target1, result1, Math.ulp(target1));


        final double target2 = lower * 7;
        final DoubleUnaryOperator func2 = x -> x - target2;
        final double result2 = solver.findRoot(func2, lower, initial, upper);
        Assertions.assertEquals(target2, result2, Math.ulp(target2));
    }

    @Test
    void testOverflowInInitialValue() {
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);

        final double lower1 = Double.MAX_VALUE / 2;
        final double upper1 = Double.MAX_VALUE;
        final double target1 = lower1 + 0.5 * (upper1 - lower1);
        final DoubleUnaryOperator func1 = x -> x - target1;
        final double result1 = solver.findRoot(func1, lower1, upper1);
        Assertions.assertEquals(target1, result1, Math.ulp(target1));


        final double lower2 = -Double.MAX_VALUE;
        final double upper2 = -Double.MAX_VALUE / 2;
        final double target2 = lower2 + 0.5 * (upper2 - lower2);
        final DoubleUnaryOperator func2 = x -> x - target2;
        final double result2 = solver.findRoot(func2, lower2, upper2);
        Assertions.assertEquals(target2, result2, Math.ulp(target2));


        final double target3 = Double.MAX_VALUE / 2;
        final DoubleUnaryOperator func3 = x -> x - target3;
        final double result3 = solver.findRoot(func3, -Double.MAX_VALUE, Double.MAX_VALUE);
        Assertions.assertEquals(target3, result3, Math.ulp(target3));
    }

    @Test
    void testMinValueNoBracket() {
        final BrentSolver solver = new BrentSolver(DEFAULT_RELATIVE_ACCURACY,
                                                   DEFAULT_ABSOLUTE_ACCURACY,
                                                   DEFAULT_FUNCTION_ACCURACY);
        final double min = Double.MIN_VALUE;


        final DoubleUnaryOperator func1 = x -> 1;
        SolverException ex1 = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(func1, min, min));
        Assertions.assertTrue(ex1.getMessage().toLowerCase(Locale.ROOT).contains("no bracket"));


        final DoubleUnaryOperator func2 = x -> 1e-100;
        Assertions.assertEquals(min, solver.findRoot(func2, min, min));


        final double lo = 2;
        final double hi = 1;
        SolverException ex2 = Assertions.assertThrows(SolverException.class,
            () -> solver.findRoot(func1, lo, hi));
        Assertions.assertEquals(String.format(SolverException.TOO_LARGE, lo, hi), ex2.getMessage());
    }






    @Test
    void testConvergenceToMachineAccuracy() {




        final BrentSolver solver = new BrentSolver(0, 1e-30, 0);
        final DoubleUnaryOperator f = x -> x * x - 2;
        final double x = Assertions.assertTimeoutPreemptively(Duration.ofMillis(500),
            () -> solver.findRoot(f, 1, 2), "Iterated too long");
        final double expected = Math.sqrt(2.0);
        Assertions.assertEquals(expected, x, Math.ulp(expected));
    }
}
