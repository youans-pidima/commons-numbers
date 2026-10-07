















package org.apache.commons.numbers.gamma;















public final class IncompleteBeta {


    private IncompleteBeta() {

    }














    public static double value(double x,
                               double a,
                               double b) {
        return BoostBeta.beta(a, b, x);
    }
















    public static double value(double x,
                               final double a,
                               final double b,
                               double epsilon,
                               int maxIterations) {
        return BoostBeta.beta(a, b, x, new Policy(epsilon, maxIterations));
    }
















    public static double complement(double x,
                                    double a,
                                    double b) {
        return BoostBeta.betac(a, b, x);
    }


















    public static double complement(double x,
                                    final double a,
                                    final double b,
                                    double epsilon,
                                    int maxIterations) {
        return BoostBeta.betac(a, b, x, new Policy(epsilon, maxIterations));
    }
}
