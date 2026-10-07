















package org.apache.commons.numbers.gamma;
















public final class RegularizedGamma {

    private RegularizedGamma() {

    }







    public static final class P {

        private P() {}









        public static double value(double a,
                                   double x) {
            return BoostGamma.gammaP(a, x);
        }











        public static double value(double a,
                                   double x,
                                   double epsilon,
                                   int maxIterations) {
            return BoostGamma.gammaP(a, x, new Policy(epsilon, maxIterations));
        }













        public static double derivative(double a,
                                        double x) {
            return BoostGamma.gammaPDerivative(a, x);
        }
    }







    public static final class Q {

        private Q() {}









        public static double value(double a,
                                   double x) {
            return BoostGamma.gammaQ(a, x);
        }











        public static double value(final double a,
                                   double x,
                                   double epsilon,
                                   int maxIterations) {
            return BoostGamma.gammaQ(a, x, new Policy(epsilon, maxIterations));
        }













        public static double derivative(double a,
                                        double x) {
            return -BoostGamma.gammaPDerivative(a, x);
        }
    }
}
