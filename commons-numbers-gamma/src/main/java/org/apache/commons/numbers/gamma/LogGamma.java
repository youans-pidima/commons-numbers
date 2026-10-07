















package org.apache.commons.numbers.gamma;













public final class LogGamma {

    private LogGamma() {

    }









    public static double value(double x) {
        return BoostGamma.lgamma(x);
    }















    public static double value(double x, int[] sign) {
        return BoostGamma.lgamma(x, sign);
    }
}
