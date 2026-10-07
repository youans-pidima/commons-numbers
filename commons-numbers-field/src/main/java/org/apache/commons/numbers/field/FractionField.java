















package org.apache.commons.numbers.field;

import org.apache.commons.numbers.fraction.Fraction;




public final class FractionField extends AbstractField<Fraction> {

    private static final FractionField INSTANCE = new FractionField();


    private FractionField() {}






    public static FractionField get() {
        return INSTANCE;
    }


    @Override
    public Fraction one() {
        return Fraction.ONE;
    }


    @Override
    public Fraction zero() {
        return Fraction.ZERO;
    }
}
