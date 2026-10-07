















package org.apache.commons.numbers.fraction;

import java.util.function.Supplier;




































public final class GeneralizedContinuedFraction {






    static final double SMALL = 1e-50;









    static final int DEFAULT_ITERATIONS = 1_000_000;















    private static final double MIN_EPSILON = 0x1.0p-53;



    private static final double MAX_EPSILON = 0.5;


    private static final double DEFAULT_LOW = 1 - MIN_EPSILON;


    private static final double DEFAULT_EPS = 0x1.0p-52;







    public static final class Coefficient {

        private final double a;

        private final double b;





        private Coefficient(double a, double b) {
            this.a = a;
            this.b = b;
        }






        public double getA() {
            return a;
        }






        public double getB() {
            return b;
        }








        public static Coefficient of(double a, double b) {
            return new Coefficient(a, b);
        }
    }


    private GeneralizedContinuedFraction() {}












    public static double value(Supplier<Coefficient> gen) {
        return value(gen, MIN_EPSILON, DEFAULT_ITERATIONS);
    }













    public static double value(Supplier<Coefficient> gen, double epsilon) {
        return value(gen, epsilon, DEFAULT_ITERATIONS);
    }


































    public static double value(Supplier<Coefficient> gen, double epsilon, int maxIterations) {


        final Coefficient c = gen.get();
        return evaluate(c.getB(), gen, epsilon, maxIterations);
    }




















    public static double value(double b0, Supplier<Coefficient> gen) {
        return value(b0, gen, MIN_EPSILON, DEFAULT_ITERATIONS);
    }





















    public static double value(double b0, Supplier<Coefficient> gen, double epsilon) {
        return value(b0, gen, epsilon, DEFAULT_ITERATIONS);
    }


























































    public static double value(double b0, Supplier<Coefficient> gen, double epsilon, int maxIterations) {



        final Coefficient c = gen.get();
        return b0 + c.getA() / evaluate(c.getB(), gen, epsilon, maxIterations);
    }



































    static double evaluate(double b0, Supplier<Coefficient> gen, double epsilon, int maxIterations) {










        final double low;
        final double eps;
        if (epsilon > MIN_EPSILON && epsilon <= MAX_EPSILON) {
            low = 1 - epsilon;
            eps = 1 / low - 1;
        } else {

            low = DEFAULT_LOW;
            eps = DEFAULT_EPS;
        }

        double hPrev = updateIfCloseToZero(b0);
















        double dPrev = 0.0;
        double cPrev = hPrev;

        for (int n = maxIterations; n > 0; n--) {
            final Coefficient c = gen.get();
            final double a = c.getA();
            final double b = c.getB();

            double dN = updateIfCloseToZero(b + a * dPrev);
            final double cN = updateIfCloseToZero(b + a / cPrev);

            dN = 1 / dN;
            final double deltaN = cN * dN;
            final double hN = hPrev * deltaN;





            if (!Double.isFinite(hN)) {
                throw new FractionException("Continued fraction diverged to " + hN);
            }






            if (deltaN == 0) {
                throw new FractionException("Ratio of successive convergents is zero");
            }





            if (Math.abs(deltaN - 1) <= eps && deltaN >= low) {
                return hN;
            }

            dPrev = dN;
            cPrev = cN;
            hPrev = hN;
        }

        throw new FractionException("Maximum iterations (%d) exceeded", maxIterations);
    }










    private static double updateIfCloseToZero(double value) {
        return Math.abs(value) < SMALL ? Math.copySign(SMALL, value) : value;
    }
}
