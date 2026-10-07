















package org.apache.commons.numbers.gamma;









public final class Erfc {

    private Erfc() {

    }



















    public static double value(double x) {
        return BoostErf.erfc(x);
    }
}
