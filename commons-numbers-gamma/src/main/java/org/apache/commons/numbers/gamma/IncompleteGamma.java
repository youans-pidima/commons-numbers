















package org.apache.commons.numbers.gamma;

















public final class IncompleteGamma {

    private IncompleteGamma() {}








    public static final class Lower {

        private Lower() {}









        public static double value(double a,
                                   double x) {
            return BoostGamma.tgammaLower(a, x);
        }











        public static double value(final double a,
                                   double x,
                                   double epsilon,
                                   int maxIterations) {
            return BoostGamma.tgammaLower(a, x, new Policy(epsilon, maxIterations));
        }
    }








    public static final class Upper {

        private Upper() {}









        public static double value(double a,
                                   double x) {
            return BoostGamma.tgamma(a, x);
        }











        public static double value(double a,
                                   double x,
                                   double epsilon,
                                   int maxIterations) {
            return BoostGamma.tgamma(a, x, new Policy(epsilon, maxIterations));
        }
    }
}
