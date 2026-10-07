















package org.apache.commons.numbers.gamma;






















public final class Gamma {

    private Gamma() {

    }







    public static double value(final double x) {
        return BoostGamma.tgamma(x);
    }
}
