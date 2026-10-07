















package org.apache.commons.numbers.field;




public final class FP64Field extends AbstractField<FP64> {

    private static final FP64 ZERO = FP64.of(0d);

    private static final FP64 ONE = FP64.of(1d);

    private static final FP64Field INSTANCE = new FP64Field();


    private FP64Field() {}






    public static FP64Field get() {
        return INSTANCE;
    }


    @Override
    public FP64 one() {
        return ONE;
    }


    @Override
    public FP64 zero() {
        return ZERO;
    }
}
