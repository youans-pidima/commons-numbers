















package org.apache.commons.numbers.field;

import org.apache.commons.numbers.fraction.BigFraction;




public final class BigFractionField extends AbstractField<BigFraction> {

    private static final BigFractionField INSTANCE = new BigFractionField();


    private BigFractionField() {}






    public static BigFractionField get() {
        return INSTANCE;
    }


    @Override
    public BigFraction one() {
        return BigFraction.ONE;
    }


    @Override
    public BigFraction zero() {
        return BigFraction.ZERO;
    }
}
