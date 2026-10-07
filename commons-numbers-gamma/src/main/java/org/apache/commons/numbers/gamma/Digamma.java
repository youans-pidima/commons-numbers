















package org.apache.commons.numbers.gamma;











public final class Digamma {

    private static final double GAMMA = 0.577215664901532860606512090082;


    private static final double C_LIMIT = 49;

    private static final double S_LIMIT = 1e-5;

    private static final double F_M1_12 = -1d / 12;

    private static final double F_1_120 = 1d / 120;

    private static final double F_M1_252 = -1d / 252;


    private Digamma() {

    }



















    public static double value(double x) {
        if (!Double.isFinite(x)) {
            return x;
        }

        double digamma = 0;
        if (x < 0) {

            digamma -= Math.PI / Math.tan(Math.PI * x);
            x = 1 - x;
        }

        if (x > 0 && x <= S_LIMIT) {

            return digamma - GAMMA - 1 / x;
        }

        while (x < C_LIMIT) {
            digamma -= 1 / x;
            x += 1;
        }


        final double inv = 1 / (x * x);



        digamma += Math.log(x) - 0.5 / x + inv * (F_M1_12 + inv * (F_1_120 + F_M1_252 * inv));

        return digamma;
    }
}
