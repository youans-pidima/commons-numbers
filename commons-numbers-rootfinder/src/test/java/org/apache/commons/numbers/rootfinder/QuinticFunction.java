















package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;




class QuinticFunction implements DoubleUnaryOperator {
    @Override
    public double applyAsDouble(double x) {
        return (x - 1) * (x - 0.5) * x * (x + 0.5) * (x + 1);
    }
}
