















package org.apache.commons.numbers.field;

import java.util.function.BiPredicate;




class FieldTestData<T> {
    private final Field<T> field;
    private final T a;
    private final T b;
    private final T c;
    private final BiPredicate<T, T> equal;

    FieldTestData(Field<T> field,
        T a,
        T b,
        T c) {
        this(field, a, b, c, Object::equals);
    }

    FieldTestData(Field<T> field,
                  T a,
                  T b,
                  T c,
                  BiPredicate<T, T> equal) {
        this.field = field;
        this.a = a;
        this.b = b;
        this.c = c;
        this.equal = equal;
    }

    public Field<T> getField() {
        return field;
    }

    public T getA() {
        return a;
    }

    public T getB() {
        return b;
    }

    public T getC() {
        return c;
    }

    @Override
    public String toString() {
        return field.toString() + " [a=" + a + ", b=" + b + ", c=" + c + "]";
    }

    public boolean equals(T x, T y) {
        return equal.test(x, y);
    }
}
