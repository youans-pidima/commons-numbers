















package org.apache.commons.numbers.core;






public interface Addition<T> {






    T add(T a);







    T zero();






    T negate();














    default boolean isZero() {
        return this.equals(zero());
    }
}
