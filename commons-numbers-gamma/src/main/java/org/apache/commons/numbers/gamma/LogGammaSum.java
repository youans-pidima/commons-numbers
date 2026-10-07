















package org.apache.commons.numbers.gamma;







final class LogGammaSum {

    private LogGammaSum() {

    }












    static double value(double a,
                        double b) {
        if (a < 1 ||
            a > 2) {
            throw new GammaException(GammaException.OUT_OF_RANGE, a, 1, 2);
        }
        if (b < 1 ||
            b > 2) {
            throw new GammaException(GammaException.OUT_OF_RANGE, b, 1, 2);
        }

        final double x = (a - 1) + (b - 1);
        if (x <= 0.5) {
            return LogGamma1p.value(1 + x);
        } else if (x <= 1.5) {
            return LogGamma1p.value(x) + Math.log1p(x);
        } else {
            return LogGamma1p.value(x - 1) + Math.log(x * (1 + x));
        }
    }
}
