















package org.apache.commons.numbers.gamma;
















public final class Beta {


    private Beta() {

    }














    public static double value(double a,
                               double b) {
        return BoostBeta.beta(a, b);
    }
}
