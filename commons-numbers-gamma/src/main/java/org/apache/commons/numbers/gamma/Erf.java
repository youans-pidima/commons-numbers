















package org.apache.commons.numbers.gamma;






public final class Erf {

    private Erf() {

    }



















    public static double value(double x) {
        return BoostErf.erf(x);
    }
}
