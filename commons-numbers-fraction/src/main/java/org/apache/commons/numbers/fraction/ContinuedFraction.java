















package org.apache.commons.numbers.fraction;

import java.util.function.Supplier;
import org.apache.commons.numbers.fraction.GeneralizedContinuedFraction.Coefficient;


























public abstract class ContinuedFraction {

    public ContinuedFraction() {}









    protected abstract double getA(int n, double x);









    protected abstract double getB(int n, double x);













    public double evaluate(double x, double epsilon) {
        return evaluate(x, epsilon, GeneralizedContinuedFraction.DEFAULT_ITERATIONS);
    }


























    public double evaluate(double x, double epsilon, int maxIterations) {



        final double b0 = getB(0, x);


        final Supplier<Coefficient> gen = new Supplier<Coefficient>() {

            private int n;
            @Override
            public Coefficient get() {
                n++;
                final double a = getA(n, x);
                final double b = getB(n, x);
                return Coefficient.of(a, b);
            }
        };

















        if (Math.abs(b0) < GeneralizedContinuedFraction.SMALL) {


            return GeneralizedContinuedFraction.value(b0, gen, epsilon, maxIterations);
        }








        return GeneralizedContinuedFraction.evaluate(b0, gen, epsilon, maxIterations);
    }
}
