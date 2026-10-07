
















package org.apache.commons.numbers.combinatorics;






@Deprecated
public final class FactorialDouble {

    private static final FactorialDouble INSTANCE = new FactorialDouble();


    private FactorialDouble() {}








    @Deprecated
    public static FactorialDouble create() {
        return INSTANCE;
    }









    @Deprecated
    public FactorialDouble withCache(final int cacheSize) {
        return this;
    }










    @Deprecated
    public double value(int n) {
        return Factorial.doubleValue(n);
    }
}
