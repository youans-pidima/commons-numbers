















package org.apache.commons.numbers.angle;

import java.util.function.DoubleUnaryOperator;







public class Reduce implements DoubleUnaryOperator {

    private final double offset;

    private final double period;







    public Reduce(double offset,
                  double period) {
        this.offset = offset;
        this.period = Math.abs(period);
    }


    @Override
    public double applyAsDouble(double x) {
        final double xMo = x - offset;
        return xMo - period * Math.floor(xMo / period);
    }
}
