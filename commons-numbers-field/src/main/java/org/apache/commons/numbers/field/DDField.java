















package org.apache.commons.numbers.field;

import org.apache.commons.numbers.core.DD;






public final class DDField extends AbstractField<DD> {

    private static final DDField INSTANCE = new DDField();


    private DDField() {}






    public static DDField get() {
        return INSTANCE;
    }


    @Override
    public DD one() {
        return DD.ONE;
    }


    @Override
    public DD zero() {
        return DD.ZERO;
    }
}
