















package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;


















public class BrentSolver {

    private final double relativeAccuracy;

    private final double absoluteAccuracy;

    private final double functionValueAccuracy;















    public BrentSolver(double relativeAccuracy,
                       double absoluteAccuracy,
                       double functionValueAccuracy) {
        this(checkAccuracy("relative", relativeAccuracy),
             checkAccuracy("absolute", absoluteAccuracy),
             checkAccuracy("function value", functionValueAccuracy),
             false);
    }











    private BrentSolver(double relativeAccuracy,
                        double absoluteAccuracy,
                        double functionValueAccuracy,
                        boolean ignored) {
        this.relativeAccuracy = relativeAccuracy;
        this.absoluteAccuracy = absoluteAccuracy;
        this.functionValueAccuracy = functionValueAccuracy;
    }














    public double findRoot(DoubleUnaryOperator function,
                           double min,
                           double max) {





        final double initial = min == max ? min : 0.5 * min + 0.5 * max;
        return findRoot(function, min, initial, max);
    }

















    public double findRoot(DoubleUnaryOperator function,
                           double min,
                           double initial,
                           double max) {
        checkFinite("min", min);
        checkFinite("max", max);
        if (min > max) {
            throw new SolverException(SolverException.TOO_LARGE, min, max);
        }
        if (initial < min ||
            initial > max) {
            throw new SolverException(SolverException.OUT_OF_RANGE, initial, min, max);
        }


        final DoubleUnaryOperator func = x -> {
            final double fx = function.applyAsDouble(x);
            if (Double.isNaN(fx)) {
                throw new SolverException(SolverException.NAN, x);
            }
            return fx;
        };


        final double yInitial = func.applyAsDouble(initial);
        if (Math.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }


        final double yMin = func.applyAsDouble(min);
        if (Math.abs(yMin) <= functionValueAccuracy) {
            return min;
        }


        if (Double.compare(yInitial * yMin, 0.0) < 0) {
            return brent(func, min, initial, yMin, yInitial);
        }


        final double yMax = func.applyAsDouble(max);
        if (Math.abs(yMax) <= functionValueAccuracy) {
            return max;
        }


        if (Double.compare(yInitial * yMax, 0.0) < 0) {
            return brent(func, initial, max, yInitial, yMax);
        }

        throw new SolverException(SolverException.BRACKETING, min, yMin, max, yMax);
    }


















    private double brent(DoubleUnaryOperator func,
                         double lo, double hi,
                         double fLo, double fHi) {
        double a = lo;
        double fa = fLo;
        double b = hi;
        double fb = fHi;
        double c = a;
        double fc = fa;
        double d = b - a;
        double e = d;

        final double t = absoluteAccuracy;
        final double eps = relativeAccuracy;

        while (true) {
            if (Math.abs(fc) < Math.abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }


            final double tol = Math.max(2 * eps * Math.abs(b) + t, Math.ulp(b));
            final double m = 0.5 * (c - b);





            if (Math.abs(m) <= tol ||
                equalsZero(fb))  {
                return b;
            }
            if (Math.abs(e) < tol ||
                Math.abs(fa) <= Math.abs(fb)) {

                d = m;
                e = d;
            } else {
                final double s = fb / fa;
                double p;
                double q;



                if (a == c) {

                    p = 2 * m * s;
                    q = 1 - s;
                } else {

                    q = fa / fc;
                    final double r = fb / fc;
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1));
                    q = (q - 1) * (r - 1) * (s - 1);
                }
                if (p > 0) {
                    q = -q;
                } else {
                    p = -p;
                }
                if (p >= 1.5 * m * q - Math.abs(tol * q) ||
                    p >= Math.abs(0.5 * e * q)) {



                    d = m;
                    e = d;
                } else {
                    e = d;
                    d = p / q;
                }
            }
            a = b;
            fa = fb;

            if (Math.abs(d) > tol) {
                b += d;
            } else if (m > 0) {
                b += tol;
            } else {
                b -= tol;
            }
            fb = func.applyAsDouble(b);
            if ((fb > 0 && fc > 0) ||
                (fb <= 0 && fc <= 0)) {
                c = a;
                fc = fa;
                d = b - a;
                e = d;
            }
        }
    }







    private static boolean equalsZero(double value) {
        return Math.abs(value) <= Double.MIN_VALUE;
    }











    private static double checkAccuracy(String name, double accuracy) {
        if (!(accuracy >= 0 && accuracy <= Double.MAX_VALUE)) {
            throw new SolverException(SolverException.INVALID_ACCURACY, name, accuracy);
        }
        return accuracy;
    }








    private static void checkFinite(String name, double value) {
        if (!Double.isFinite(value)) {
            throw new SolverException(SolverException.NON_FINITE, name, value);
        }
    }
}
