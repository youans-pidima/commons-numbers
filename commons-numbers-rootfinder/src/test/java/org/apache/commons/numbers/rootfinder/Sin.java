















package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;




class Sin implements DoubleUnaryOperator {
    @Override
    public double applyAsDouble(double x) {
        return Math.sin(x);
    }
}
