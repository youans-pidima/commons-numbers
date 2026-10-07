















package org.apache.commons.numbers.core;






public interface Multiplication<T> {






    T multiply(T a);







    T one();






    T reciprocal();














    default boolean isOne() {
        return this.equals(one());
    }
}
