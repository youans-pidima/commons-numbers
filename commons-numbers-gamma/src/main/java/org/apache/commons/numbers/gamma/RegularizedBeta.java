















package org.apache.commons.numbers.gamma;
















public final class RegularizedBeta {


    private RegularizedBeta() {

    }
















    public static double value(double x,
                               double a,
                               double b) {
        return BoostBeta.ibeta(a, b, x);
    }


















    public static double value(double x,
                               final double a,
                               final double b,
                               double epsilon,
                               int maxIterations) {
        return BoostBeta.ibeta(a, b, x, new Policy(epsilon, maxIterations));
    }















    public static double complement(double x,
                                    double a,
                                    double b) {
        return BoostBeta.ibetac(a, b, x);
    }

















    public static double complement(double x,
                                    final double a,
                                    final double b,
                                    double epsilon,
                                    int maxIterations) {
        return BoostBeta.ibetac(a, b, x, new Policy(epsilon, maxIterations));
    }



















    public static double derivative(double x,
                                    double a,
                                    double b) {
        return BoostBeta.ibetaDerivative(a, b, x);
    }
}
