















package org.apache.commons.numbers.core;







public interface NativeOperators<T>
    extends Addition<T>,
            Multiplication<T> {






    T subtract(T a);







    T divide(T a);







    T multiply(int n);







    T pow(int n);
}
