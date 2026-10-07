















package org.apache.commons.numbers.rootfinder;

import java.util.function.DoubleUnaryOperator;




class MonitoredFunction implements DoubleUnaryOperator {
    private final int maxCount;
    private int callsCount;
    private final DoubleUnaryOperator f;

    MonitoredFunction(DoubleUnaryOperator f) {
        this(f, Integer.MAX_VALUE);
    }

    MonitoredFunction(DoubleUnaryOperator f,
                      int maxCount) {
        callsCount = 0;
        this.f = f;
        this.maxCount = maxCount;
    }

    int getCallsCount() {
        return callsCount;
    }

    @Override
    public double applyAsDouble(double x) {
        if (++callsCount > maxCount) {
            throw new IllegalStateException(callsCount + " > " + maxCount +
                                            ": too many calls");
        }
        return f.applyAsDouble(x);
    }
}
