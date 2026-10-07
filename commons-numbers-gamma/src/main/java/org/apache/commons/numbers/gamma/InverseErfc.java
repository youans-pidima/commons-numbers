















package org.apache.commons.numbers.gamma;




public final class InverseErfc {

    private InverseErfc() {

    }
















    public static double value(double x) {
        return BoostErf.erfcInv(x);
    }
}
