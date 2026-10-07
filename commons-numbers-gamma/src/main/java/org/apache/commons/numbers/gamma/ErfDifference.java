















package org.apache.commons.numbers.gamma;




public final class ErfDifference {











    private static final double X_CRIT = 0.47693627620446993;


    private ErfDifference() {

    }










    public static double value(double x1,
                               double x2) {
        if (x1 > x2) {
            return -value(x2, x1);
        }
        if (x1 < -X_CRIT) {
            if (x2 < 0) {
                return Erfc.value(-x2) - Erfc.value(-x1);
            }
            return Erf.value(x2) - Erf.value(x1);
        }
        if (x2 > X_CRIT &&
            x1 > 0) {
            return Erfc.value(x1) - Erfc.value(x2);
        }
        return Erf.value(x2) - Erf.value(x1);
    }
}
