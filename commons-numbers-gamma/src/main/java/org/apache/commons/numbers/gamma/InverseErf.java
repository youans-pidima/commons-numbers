















package org.apache.commons.numbers.gamma;




public final class InverseErf {

    private InverseErf() {

    }
















    public static double value(final double x) {
        return BoostErf.erfInv(x);
    }
}
