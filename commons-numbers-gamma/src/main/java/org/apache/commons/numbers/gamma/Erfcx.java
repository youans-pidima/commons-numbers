















package org.apache.commons.numbers.gamma;













public final class Erfcx {

    private Erfcx() {

    }















    public static double value(double x) {
        return BoostErf.erfcx(x);
    }
}
