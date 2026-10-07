















package org.apache.commons.numbers.gamma;







public final class Trigamma {

    private static final double C_LIMIT = 49;


    private static final double S_LIMIT = 1e-5;

    private static final double F_1_6 = 1d / 6;

    private static final double F_1_30 = 1d / 30;

    private static final double F_1_42 = 1d / 42;


    private Trigamma() {

    }







    public static double value(double x) {
        if (!Double.isFinite(x)) {
            return x;
        }

        if (x < 0) {








            if (Math.rint(x) == x) {
                return Double.POSITIVE_INFINITY;
            }

            final double s = Math.sin(Math.PI * x);
            return Math.PI * Math.PI / (s * s) - value(1 - x);
        }

        if (x > 0 && x <= S_LIMIT) {
            return 1 / (x * x);
        }

        double trigamma = 0;
        while (x < C_LIMIT) {
            trigamma += 1 / (x * x);
            x += 1;
        }

        final double inv = 1 / (x * x);



        trigamma += 1 / x + 0.5 * inv + inv / x * (F_1_6 - inv * (F_1_30 + F_1_42 * inv));

        return trigamma;
    }
}
