















package org.apache.commons.numbers.gamma;


















public final class GammaRatio {

    private GammaRatio() {

    }












    public static double value(double a, double b) {
        return BoostGamma.tgammaRatio(a, b);
    }














    public static double delta(double a, double delta) {
        return BoostGamma.tgammaDeltaRatio(a, delta);
    }
}
