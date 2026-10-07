















package org.apache.commons.numbers.gamma;






final class LogGamma1p {

    private LogGamma1p() {

    }











    static double value(final double x) {
        if (x < -0.5 || x > 1.5) {
            throw new GammaException(GammaException.OUT_OF_RANGE, x, -0.5, 1.5);
        }

        return -Math.log1p(InvGamma1pm1.value(x));
    }
}
