















package org.apache.commons.numbers.field;

import org.apache.commons.numbers.core.NativeOperators;






public abstract class AbstractField<T extends NativeOperators<T>>
    implements Field<T> {

    public AbstractField() {}


    @Override
    public T add(T a, T b) {
        return a.add(b);
    }


    @Override
    public T subtract(T a, T b) {
        return a.subtract(b);
    }


    @Override
    public T negate(T a) {
        return a.negate();
    }


    @Override
    public T multiply(int n, T a) {
        return a.multiply(n);
    }


    @Override
    public T multiply(T a, T b) {
        return a.multiply(b);
    }


    @Override
    public T divide(T a, T b) {
        return a.divide(b);
    }


    @Override
    public T reciprocal(T a) {
        return a.reciprocal();
    }
}
